package com.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class GenerateRule {
    public static void main(String[] args) {
        InputStream inputStream = GenerateRule.class.getClassLoader().getResourceAsStream("data.json");
        String jsonString = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining());

        JSONArray arr = new JSONArray(jsonString);
        for (int i = 0; i < arr.length(); i++) {
            System.out.println("Rule Number: " + (i + 1));
            String result = build(arr.getJSONObject(i));
            System.out.println(result);
        }
    }

    private static String build(JSONObject jsonObject) {
        StringBuilder builder = new StringBuilder();
        try {
            JSONArray itemCondition = jsonObject.getJSONArray("itemCondition");

            for (int i = 0; i < itemCondition.length(); i++) {
                JSONArray items = itemCondition.getJSONObject(i).getJSONArray("items");
                String condition = itemCondition.getJSONObject(i).getString("condition");

                builder.append(buildCondition(condition));
                builder.append("(");
                for (int j = 0; j < items.length(); j++) {
                    String result = build(items.getJSONObject(j));
                    builder.append(result);
                }
                builder.append(")");
            }
        } catch (Exception ex) {
            String condition = jsonObject.getString("condition");
            builder.append(buildCondition(condition));
            builder.append(buildObject(jsonObject));
        }

        return builder.toString();
    }

    private static String buildCondition(String condition) {
        if (condition == null || condition.isEmpty()) {
            return "";
        }

        return String.format(" %s ", condition);
    }

    private static String buildObject(JSONObject jsonObject) {
        String left = jsonObject.getString("left");
        String right = jsonObject.getString("right");
        String operator = jsonObject.getString("operator");

        return String.format("%s %s %s", left, operator, right);
    }
}
