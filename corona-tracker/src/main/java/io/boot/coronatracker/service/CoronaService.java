package io.boot.coronatracker.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.boot.coronatracker.bean.CoronaObject;

@Service
public class CoronaService {
	
	private static String virusDataUrl = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

	private List<CoronaObject> totalCasesList = new ArrayList<>();
	// This method will be executed as soon as instance is created
	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void getCoronaDetails() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(virusDataUrl)).build();
		
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		
		//System.out.println(response.body());
		
StringReader reader = new StringReader(response.body());
		
Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
		List<CoronaObject> currentList = new ArrayList<CoronaObject>();
		for (CSVRecord record : records) {
			CoronaObject object = new CoronaObject();
			object.setState(record.get(0));
			object.setCountry(record.get(1));
			int totalCases = Integer.parseInt(record.get(record.size()-1));
			object.setLatestTotalCases(totalCases);
//			System.out.println(object);
			currentList.add(object);
		}
		
		this.totalCasesList = currentList;
		System.out.println(totalCasesList);
		
		reader.close();
	}
	public List<CoronaObject> getTotalCasesList() {
		return totalCasesList;
	}
	
}
