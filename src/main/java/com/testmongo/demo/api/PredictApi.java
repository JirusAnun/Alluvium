package com.testmongo.demo.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.testmongo.demo.business.PredictBusiness;
import com.testmongo.demo.exception.BaseException;
import com.testmongo.demo.model.LocationRequest;
import com.testmongo.demo.model.PredictRes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping()
public class PredictApi {

    private final PredictBusiness predictBusiness;
    public PredictApi(PredictBusiness predictBusiness) { this.predictBusiness = predictBusiness; }

    @PostMapping("/predict")
    public ResponseEntity<ArrayList<PredictRes>> predict(@RequestBody List<LocationRequest> locationRequests) throws BaseException, JsonProcessingException {
        ArrayList<PredictRes> respond = new ArrayList<>();
        for (LocationRequest request : locationRequests) {
            predictBusiness.predict(request.getTop(), request.getBtm(), respond);
        }
        Collections.sort(respond);
        return ResponseEntity.ok(respond);
    }

}
