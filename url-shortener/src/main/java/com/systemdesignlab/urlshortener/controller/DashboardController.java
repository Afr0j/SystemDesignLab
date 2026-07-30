package com.systemdesignlab.urlshortener.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.systemdesignlab.urlshortener.dto.DashboardResponse;
import com.systemdesignlab.urlshortener.service.DashboardService;

@RestController
@RequestMapping("/api/v1")
//@RequestMapping("/api/v1/urls")
public class DashboardController {
	
	private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    
    @GetMapping("/dashboard")
	public ResponseEntity<DashboardResponse> dashboard(){
		return ResponseEntity.ok(
				new DashboardResponse(dashboardService.totalUrls(),dashboardService.totalRedirects()
						,dashboardService.averageClicks(),dashboardService.mostClickedUrl())				
				);
	}
    
  
}
