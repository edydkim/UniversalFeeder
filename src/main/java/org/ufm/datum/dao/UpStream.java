package org.ufm;

import java.util.ArrayList;
import java.util.List;


public class UpStream<S> {
    // A List of Stream
    List<S> streams = new ArrayList<>();

    private S stream;

    public UpStream() {
    }

    public S getStream() {
        return stream;
    }

    public void setStream(S stream) {
        this.stream = stream;
    }

    public List<S> getStreams() {
        return streams;
    }

    public void setStreams(List<S> streams) {
        this.streams = streams;
    }

    public void add(S stream) {
        streams.add(stream);
    }
}
