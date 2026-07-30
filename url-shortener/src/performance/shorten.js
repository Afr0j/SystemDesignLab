import http from "k6/http";
import { check } from "k6";

export const options = {
    vus: 5,
    duration: "10s",
};

export default function () {

    const payload = JSON.stringify({
        url: "https://google.com"
    });

    const params = {
        headers: {
            "Content-Type": "application/json"
        }
    };

    const response = http.post(
        "http://localhost:8080/api/v1/urls/shorten",
        payload,
        params
    );

    console.log("Status:", response.status);
    console.log("Body:", response.body);

 /*   check(response, {
        "Status is 200": (r) => r.status === 200
    });*/
	check(response, {
	    "status is 200": (r) => r.status === 200,
	    "body contains shortUrl": (r) => {
	        try {
	            return JSON.parse(r.body).shortUrl !== undefined;
	        } catch {
	            return false;
	        }
	    }
	});

	if (response.status !== 200) {
	    console.log(`Status: ${response.status}`);
	    console.log(response.body);
	}
}