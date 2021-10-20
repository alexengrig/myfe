package dev.alexengrig.myfe.util;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("ResultOfMethodCallIgnored")
class CloseOnTerminalOperationStreamsTest {

    AtomicBoolean closeHolder = new AtomicBoolean(false);

    Stream<String> stream() {
        return Stream.of("1", "2", "3").onClose(() -> closeHolder.set(true));
    }

    Stream<String> wrappedStream() {
        return CloseOnTerminalOperationStreams.wrap(stream());
    }

    @Test
    void testWithoutTryWithResources() {
        Stream<String> stream = stream();
        assertFalse(closeHolder.get(), "It's closed");
        stream.count();
        assertFalse(closeHolder.get(), "It's closed");
        stream.close();
        assertTrue(closeHolder.get(), "It isn't closed");
    }

    @Test
    void testWithTryWithResources() {
        try (Stream<String> ignore = stream()) {
            assertFalse(closeHolder.get(), "It's closed");
        }
        assertTrue(closeHolder.get(), "It isn't closed");
    }

    @Test
    void testWrap() {
        Stream<String> stream = wrappedStream();
        assertFalse(closeHolder.get(), "It's closed");
        stream.count();
        assertTrue(closeHolder.get(), "It isn't closed");
    }

    //TODO: Add other tests

}