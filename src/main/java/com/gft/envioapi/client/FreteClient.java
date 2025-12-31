package com.gft.envioapi.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gft.envioapi.configuration.MelhorEnvioFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "melhorEnvioClient",
        url = "${melhorenvio.base-url}",
        configuration = MelhorEnvioFeignConfig.class
)
public interface FreteClient {

    @PostMapping(
            value = "/api/v2/me/shipment/calculate",
            consumes = "application/json",
            produces = "application/json"
    )
    List<QuoteResponse> calcular(@RequestBody CalculateRequest request);

    record CalculateRequest(
            @JsonProperty("from") FromAddress from,
            @JsonProperty("to") ToAddress to,
            @JsonProperty("package") PackageInfo packageInfo,
            @JsonProperty("services") String services
    ) {}

    record FromAddress(
            @JsonProperty("postal_code") String postalCode
    ) {}

    record ToAddress(
            @JsonProperty("postal_code") String postalCode
    ) {}

    record PackageInfo(
            @JsonProperty("height") double height,
            @JsonProperty("width") double width,
            @JsonProperty("length") double length,
            @JsonProperty("weight") double weight
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record QuoteResponse(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("company") Company company,
            @JsonProperty("service") Service service,
            @JsonProperty("price") String price,
            @JsonProperty("custom_price") String customPrice,
            @JsonProperty("delivery_time") Integer deliveryTime,
            @JsonProperty("custom_delivery_time") Integer customDeliveryTime,
            @JsonProperty("error") String error
    ) {

        public String getPrecoEfetivo() {
            return (customPrice != null && !customPrice.isBlank()) ? customPrice : price;
        }

        public Integer getPrazoEfetivo() {
            return (customDeliveryTime != null) ? customDeliveryTime : deliveryTime;
        }

        public boolean isValid() {
            return (error == null || error.isBlank())
                    && getPrecoEfetivo() != null
                    && !getPrecoEfetivo().isBlank();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Company(
            @JsonProperty("name") String name
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Service(
            @JsonProperty("name") String name
    ) {}
}