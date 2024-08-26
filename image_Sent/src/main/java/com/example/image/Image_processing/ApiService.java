package com.example.image.Image_processing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ApiService {

    private final RestTemplate restTemplate;

    @Autowired
    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String callAnotherApi(String baseUrl,String path) {
        // Construct the full URL with the query parameter
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)

                .queryParam("imgPath",path)
                .toUriString();

        // Make the API call
        return restTemplate.postForObject(url, null, String.class);
    }
}

