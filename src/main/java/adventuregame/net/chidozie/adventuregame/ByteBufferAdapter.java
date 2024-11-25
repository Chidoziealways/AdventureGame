package adventuregame.net.chidozie.adventuregame;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.nio.ByteBuffer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ByteBufferAdapter extends TypeAdapter<ByteBuffer> {
    @Override
    public void write(JsonWriter out, ByteBuffer value) throws IOException {
        out.beginArray();
        while (value.hasRemaining()) {
            out.value(value.get());
        }
        out.endArray();
        value.rewind();
    }

    @Override
    public ByteBuffer read(JsonReader in) throws IOException {
        in.beginArray();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (in.hasNext()) {
            baos.write(in.nextInt());
        }
        in.endArray();
        return ByteBuffer.wrap(baos.toByteArray());
    }
}