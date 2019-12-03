package org.miaohong.newfishchatserver.core.rpc.network;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;

import java.lang.reflect.Field;

public class NettyBufferPool extends PooledByteBufAllocator {

    private static final Logger LOG = LoggerFactory.getLogger(NettyBufferPool.class);
    private static final boolean PREFER_DIRECT = true;
    private static final int PAGE_SIZE = 8192;
    private static final int MAX_ORDER = 11;
    private final Object[] directArenas;
    private final int numberOfArenas;
    private final int chunkSize;

    public NettyBufferPool(int numberOfArenas) {
        super(
                PREFER_DIRECT,
                // No heap arenas, please.
                0,
                // Number of direct arenas. Each arena allocates a chunk of 16 MB, i.e.
                // we allocate numDirectArenas * 16 MB of direct memory. This can grow
                // to multiple chunks per arena during runtime, but this should only
                // happen with a large amount of connections per task manager. We
                // control the memory allocations with low/high watermarks when writing
                // to the TCP channels. Chunks are allocated lazily.
                numberOfArenas,
                PAGE_SIZE,
                MAX_ORDER);

        Preconditions.checkArgument(numberOfArenas >= 1, "Number of arenas");
        this.numberOfArenas = numberOfArenas;

        // Arenas allocate chunks of pageSize << maxOrder bytes. With these
        // defaults, this results in chunks of 16 MB.

        this.chunkSize = PAGE_SIZE << MAX_ORDER;

        Object[] allocDirectArenas = null;
        try {
            Field directArenasField = PooledByteBufAllocator.class
                    .getDeclaredField("directArenas");
            directArenasField.setAccessible(true);

            allocDirectArenas = (Object[]) directArenasField.get(this);
        } catch (Exception ignored) {
            LOG.warn("Memory statistics not available");
        } finally {
            this.directArenas = allocDirectArenas;
        }
    }

    int getNumberOfArenas() {
        return numberOfArenas;
    }

    int getChunkSize() {
        return chunkSize;
    }

    public Option<Long> getNumberOfAllocatedBytes()
            throws NoSuchFieldException, IllegalAccessException {

        if (directArenas != null) {
            long numChunks = 0;
            for (Object arena : directArenas) {
                numChunks += getNumberOfAllocatedChunks(arena, "qInit");
                numChunks += getNumberOfAllocatedChunks(arena, "q000");
                numChunks += getNumberOfAllocatedChunks(arena, "q025");
                numChunks += getNumberOfAllocatedChunks(arena, "q050");
                numChunks += getNumberOfAllocatedChunks(arena, "q075");
                numChunks += getNumberOfAllocatedChunks(arena, "q100");
            }

            long allocatedBytes = numChunks * chunkSize;
            return Option.apply(allocatedBytes);
        } else {
            return Option.empty();
        }
    }

    private long getNumberOfAllocatedChunks(Object arena, String chunkListFieldName)
            throws NoSuchFieldException, IllegalAccessException {

        // Each PoolArena<ByteBuffer> stores its allocated PoolChunk<ByteBuffer>
        // instances grouped by usage (field qInit, q000, q025, etc.) in
        // PoolChunkList<ByteBuffer> lists. Each list has zero or more
        // PoolChunk<ByteBuffer> instances.

        // Chunk list of arena
        Field chunkListField = arena.getClass().getSuperclass()
                .getDeclaredField(chunkListFieldName);
        chunkListField.setAccessible(true);
        Object chunkList = chunkListField.get(arena);

        // Count the chunks in the list
        Field headChunkField = chunkList.getClass().getDeclaredField("head");
        headChunkField.setAccessible(true);
        Object headChunk = headChunkField.get(chunkList);

        if (headChunk == null) {
            return 0;
        } else {
            int numChunks = 0;

            Object current = headChunk;

            while (current != null) {
                Field nextChunkField = headChunk.getClass().getDeclaredField("next");
                nextChunkField.setAccessible(true);
                current = nextChunkField.get(current);
                numChunks++;
            }

            return numChunks;
        }
    }

    // ------------------------------------------------------------------------
    // Prohibit heap buffer allocations
    // ------------------------------------------------------------------------

    @Override
    public ByteBuf heapBuffer() {
        throw new UnsupportedOperationException("Heap buffer");
    }

    @Override
    public ByteBuf heapBuffer(int initialCapacity) {
        throw new UnsupportedOperationException("Heap buffer");
    }

    @Override
    public ByteBuf heapBuffer(int initialCapacity, int maxCapacity) {
        throw new UnsupportedOperationException("Heap buffer");
    }

    @Override
    public CompositeByteBuf compositeHeapBuffer() {
        throw new UnsupportedOperationException("Heap buffer");
    }

    @Override
    public CompositeByteBuf compositeHeapBuffer(int maxNumComponents) {
        throw new UnsupportedOperationException("Heap buffer");
    }
}
