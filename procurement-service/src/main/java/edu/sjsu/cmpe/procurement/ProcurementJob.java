package edu.sjsu.cmpe.procurement;

import javax.jms.Connection;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.Every;
import edu.sjsu.cmpe.procurement.domain.BookRequest;
import edu.sjsu.cmpe.procurement.domain.ShippedBook;
import edu.sjsu.cmpe.procurement.stomp.ApolloSTOMP;

@Every("300s")
public class ProcurementJob extends Job{
        @Override
        public void doJob() {
                
                
                //  GET Message from queue, POST request to Publisher
                 
               // testing System.out.println("Job 1 begins..."); 
                ApolloSTOMP apolloSTOMP = new ApolloSTOMP();
            BookRequest bookRequest;
            Connection connection;
            try {
                    connection = apolloSTOMP.makeConnection();
                        bookRequest = apolloSTOMP.reveiveQueueMessage(connection);
                    connection.close();
                    if (bookRequest.getOrder_book_isbns().size() != 0){
                            System.out.println("Posting to Publisher");
                        Client client = Client.create();
                        String url = "http://54.215.210.214:9000/orders";        
                            WebResource webResource = client.resource(url);
                            ClientResponse response = webResource.accept("application/json")
                                            .type("application/json").entity(bookRequest, "application/json").post(ClientResponse.class);
                            System.out.println(response.getEntity(String.class));
                        }
                        } catch ( Exception e) {
                                e.printStackTrace();
                        }        
           //testing ends System.out.println("Job 1 ends!");
            
            //  GET repsonse from Publisher and Publish message to Topic
              
           //testing begins System.out.println("Job 2 begins...");
            try {
                    String message;
                    Client client = Client.create();
                    String url = "http://54.215.210.214:9000/orders/67399";        
                    WebResource webResource = client.resource(url);
                    ShippedBook response = webResource.accept("application/json")
                                    .type("application/json").get(ShippedBook.class);
                    connection = apolloSTOMP.makeConnection();
                    apolloSTOMP.publishTopicMessage(connection, response);
                     
                        } catch ( Exception e) {
                                e.printStackTrace();
                        }
            // testing ends System.out.println("Job 2 ends!");
            }
}