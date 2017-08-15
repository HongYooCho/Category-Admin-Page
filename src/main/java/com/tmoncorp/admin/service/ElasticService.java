package com.tmoncorp.admin.service;

import com.tmoncorp.admin.domain.SynonymCategory;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class ElasticService {
    static final String IP = "127.0.0.1";
    static final int PORT_NUMBER = 9300;
    private TransportClient client;

    public ElasticService() {
        connectWithElastic();
    }

    public void makeIndex(String indexName, String indexType, String id, SynonymCategory synonymCategory) {
        try {
            IndexResponse response = client.prepareIndex(indexName, indexType, id)
                    .setSource(jsonBuilder()
                            .startObject()
                            .field("catNo", synonymCategory.getCatNo())
                            .field("firstCatName", synonymCategory.getFirstCatName())
                            .field("secondCatName", synonymCategory.getSecondCatName())
                            .field("thirdCatName", synonymCategory.getThirdCatName())
                            .field("fourthCatName", synonymCategory.getFourthCatName())
                            .endObject()
                    )
                    .get();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // todo : mk dow
    private void connectWithElastic() {
        try {
            Settings settings = Settings.builder()
                    .put("cluster.name", "elasticsearch").build();

            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(IP), PORT_NUMBER));


        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
