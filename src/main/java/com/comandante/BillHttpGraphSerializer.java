package com.comandante;

import com.comandante.http.server.resource.BillHttpGraph;
import com.google.gson.GsonBuilder;
import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public class BillHttpGraphSerializer implements Serializer<BillHttpGraph>, Serializable {

    @Override
    public void serialize(DataOutput dataOutput, BillHttpGraph billHttpGraph) throws IOException {
        dataOutput.writeUTF(new GsonBuilder().create().toJson(billHttpGraph, BillHttpGraph.class));
    }

    @Override
    public BillHttpGraph deserialize(DataInput dataInput, int i) throws IOException {
        return new GsonBuilder().create().fromJson(dataInput.readUTF(), BillHttpGraph.class);
    }

    @Override
    public int fixedSize() {
        return -1;
    }
}
