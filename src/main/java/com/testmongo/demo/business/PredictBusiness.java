package com.testmongo.demo.business;

import com.testmongo.demo.exception.BaseException;
import com.testmongo.demo.model.Location;
import com.testmongo.demo.model.PredictRes;
import com.testmongo.demo.service.PredictService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PredictBusiness {

    private final PredictService predictService;

    public PredictBusiness(PredictService predictService) {
        this.predictService = predictService;
    }

    public void predict(String topLeft, String btmRight, ArrayList<PredictRes> respond) throws BaseException {

        ArrayList<ArrayList<Location>> locations = new ArrayList<>();
        String[] requestList = {"hospital", "police", "school", "university", "bank", "gas", "fitness", "market_mall", "pharmacy", "restaurant", "park"};

        for (int i = 0; i < 11; ++i) {
            ArrayList<Location> location = new ArrayList<>();
            location = predictService.setStructure(topLeft, btmRight, 100, requestList[i], i);
            locations.add(location);
        }

        ArrayList<Double> score = predictService.scoringModel(locations);
        double[] ad_dis = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < 11; i++) {
            ad_dis[i] = score.get(i + 1);
        }
        for (int i = 0; i < locations.size(); i++) {
            if (!locations.get(i).isEmpty()) {
                ArrayList<Location> buff = new ArrayList<Location>();
                buff.add(locations.get(i).get(0));
                locations.set(i, buff);
            } else {
                locations.set(i, null);
            }
        }
        PredictRes res = new PredictRes(score.get(0), ad_dis, topLeft, btmRight, locations);
        respond.add(res);
    }

}
