package com.systemdesignlab.urlshortener.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {

    private long totalUrls;

    private long totalRedirects;

    private double averageClicks;

    private String mostClickedUrl;

}
