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
	private static String virusRecoveredUrl = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";
	private static String virusDeathUrl = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
	private int deaths;
	private int recovers;
	
	private List<CoronaObject> totalCasesList = new ArrayList<>();
	private List<CoronaObject> aDayBeforeTotalList = new ArrayList<>();
	// This method will be executed as soon as instance is created
	
	
	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void getCoronaDetails() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(virusDataUrl)).build();
		
		HttpRequest requestDeaths = HttpRequest.newBuilder().uri(URI.create(virusDeathUrl)).build();
		
		HttpRequest requestRecovers = HttpRequest.newBuilder().uri(URI.create(virusRecoveredUrl)).build();
		
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		
		HttpResponse<String> responseDeaths = client.send(requestDeaths, HttpResponse.BodyHandlers.ofString());
		
		HttpResponse<String> responseRecovers = client.send(requestRecovers, HttpResponse.BodyHandlers.ofString());
		
		//System.out.println(response.body());
		
		StringReader reader = new StringReader(response.body());
		
		StringReader readerDeaths = new StringReader(responseDeaths.body());
		
		StringReader readerRecovers = new StringReader(responseRecovers.body());
		
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
		List<CoronaObject> currentList = new ArrayList<CoronaObject>();
		List<CoronaObject> aDayBeforeList = new ArrayList<>();
		for (CSVRecord record : records) {
			CoronaObject object = new CoronaObject();
			object.setState(record.get(0));
			object.setCountry(record.get(1));
			int totalCases = Integer.parseInt(record.get(record.size()-1));
			object.setLatestTotalCases(totalCases);
//			System.out.println(object);
			currentList.add(object);
			CoronaObject object1 = new CoronaObject();
			object1.setState(record.get(0));
			object1.setCountry(record.get(1));
			int totalCases1 = Integer.parseInt(record.get(record.size()-2));
			object1.setLatestTotalCases(totalCases1);
			aDayBeforeList.add(object1);
			
		}
		
		Iterable<CSVRecord> recordsDeaths = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(readerDeaths);
		
		int dead = 0;
		
		for (CSVRecord record : recordsDeaths) {
			 dead= dead + Integer.parseInt(record.get(record.size()-1));	
		}
		
		this.deaths = dead;

		Iterable<CSVRecord> recordsRecovers = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(readerRecovers);
		
		int rec = 0;
		
		for (CSVRecord record : recordsRecovers) {
			rec = rec + Integer.parseInt(record.get(record.size()-1));
		}
		
		this.recovers = rec;
		
		
		this.aDayBeforeTotalList = aDayBeforeList;
		this.totalCasesList = currentList;
//		System.out.println(totalCasesList);
		System.out.println(aDayBeforeTotalList);
		
		reader.close();
	}
	public int getDeaths() {
		return deaths;
	}
	public int getRecovers() {
		return recovers;
	}
	public List<CoronaObject> getTotalCasesList() {
		return totalCasesList;
	}
	public List<CoronaObject> getaDayBeforeTotalList() {
		return aDayBeforeTotalList;
	}
	
}
