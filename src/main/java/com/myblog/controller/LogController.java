package com.myblog.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.myblog.model.FanPie;
import com.myblog.model.TopTen;
import com.myblog.util.IPUtils;
import com.myblog.util.JedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Zephery on 2017/6/23.
 */
@Controller
public class LogController {
    private final static Logger logger = LoggerFactory.getLogger(LogController.class);

    @RequestMapping("log")
    public ModelAndView log(HttpServletRequest request) {
        JedisUtil jedis=JedisUtil.getInstance();    //remember not to close
        String temp = jedis.get("daterange");
        String pv_count = jedis.get("pv_count");
        String visitor_count = jedis.get("visitor_count");
        String ip_count = jedis.get("ip_count");
        String bounce_ratio = jedis.get("bounce_ratio");
        String avg_visit_time = jedis.get("avg_visit_time");
        String top_ten = jedis.get("top_ten");
        String source = jedis.get("source");
        String rukou_str = jedis.get("rukouyemian");
        String diyu_str = jedis.get("diyu");
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        //前十访问页面
        JsonArray array = parser.parse(top_ten).getAsJsonArray();
        List<TopTen> topTens = new ArrayList<>();
        for (JsonElement element : array) {
            TopTen topTen = gson.fromJson(element, TopTen.class);
            topTens.add(topTen);
        }
        //来源统计
        JsonArray sourcearray = parser.parse(source).getAsJsonArray();
        List<FanPie> sourcelist = new ArrayList<>();
        for (JsonElement element : sourcearray) {
            FanPie fanPie = gson.fromJson(element, FanPie.class);
            sourcelist.add(fanPie);
        }
        //前十入口页面
        JsonArray rukouarray = parser.parse(rukou_str).getAsJsonArray();
        List<TopTen> rukou = new ArrayList<>();
        for (JsonElement element : rukouarray) {
            TopTen topTen = gson.fromJson(element, TopTen.class);
            rukou.add(topTen);
        }
        //地域地图
        JsonArray diyuarray = parser.parse(diyu_str).getAsJsonArray();
        List<TopTen> diyu = new ArrayList<>();
        for (JsonElement element : diyuarray) {
            TopTen topTen = gson.fromJson(element, TopTen.class);
            diyu.add(topTen);
        }
        rukou.sort(new Comparator<TopTen>() {
            @Override
            public int compare(TopTen o1, TopTen o2) {
                if (o1.getPv_count() > o2.getPv_count()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        diyu.sort(new Comparator<TopTen>() {
            @Override
            public int compare(TopTen o1, TopTen o2) {
                if (o1.getPv_count() > o2.getPv_count()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        ModelAndView modelAndView = new ModelAndView();
        String ip = IPUtils.getIpAddr(request);
        String yourcity = IPUtils.getAddressByIP(ip);
        modelAndView.addObject("ip", ip);
        modelAndView.addObject("yourcity", yourcity);
        modelAndView.addObject("daterange", parser.parse(temp).getAsJsonArray());
        modelAndView.addObject("topTens", topTens);
        modelAndView.addObject("pv_count", pv_count);
        modelAndView.addObject("visitor_count", visitor_count);
        modelAndView.addObject("ip_count", ip_count);
        modelAndView.addObject("bounce_ratio", bounce_ratio);
        modelAndView.addObject("sourcelist", sourcelist);
        modelAndView.addObject("rukou", rukou.subList(0, rukou.size() > 5 ? 5 : rukou.size()));
        modelAndView.addObject("avg_visit_time", avg_visit_time);
        modelAndView.addObject("diyu", diyu);
        modelAndView.addObject("diyumax", diyu.get(0).getPv_count());
        modelAndView.setViewName("log");
        return modelAndView;
    }
}
