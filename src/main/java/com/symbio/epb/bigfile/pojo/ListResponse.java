package com.symbio.epb.bigfile.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@SuppressWarnings("deprecation")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ListResponse<T> extends BaseResponse {
    private static final long serialVersionUID = -3168739985700077224L;
    private List<T> results;

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
