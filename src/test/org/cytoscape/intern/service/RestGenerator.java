package test.org.cytoscape.intern.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.databind.ObjectMapper;


public class RestGenerator implements Generator {

	@Override
	public InputStream generateCartesianStream(InputStream cxNodeInputStream,
			String algorithm) {
		InputStream cartesianLayout = null;
		ByteArrayOutputStream inputNetwork = new ByteArrayOutputStream();
		int next = 0;
		while (next != -1) {
			try {
				next = cxNodeInputStream.read();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
			if (next != -1) {
				inputNetwork.write(next);
			}
		}
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("http://0.0.0.0/v1/services/layout");
		try {
			httpPost.setEntity(new StringEntity(String.format("{\"network\" : \"%s\", \"algorithm\" : \"%s\"}", inputNetwork.toString(), algorithm), ContentType.APPLICATION_JSON));
		} catch (UnsupportedCharsetException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		String jobId = null;
		try {
			CloseableHttpResponse response = httpClient.execute(httpPost);
			try {
				HttpEntity responseData = response.getEntity();
				ObjectMapper mapper = new ObjectMapper();
				@SuppressWarnings("unchecked")
				Map<String, String> data = mapper.readValue(responseData.getContent(), Map.class);
				jobId = data.get("job_id");
				System.out.println(String.format("Got REST job id: %s", jobId));
			} finally {
				response.close();
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String status = null;
		do {
			try { Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
			HttpGet httpGet = new HttpGet(String.format("http://0.0.0.0/v1/queue/%s", jobId));
			try {
				CloseableHttpResponse response = httpClient.execute(httpGet);
				try {
					HttpEntity responseData = response.getEntity();
					ObjectMapper mapper = new ObjectMapper();
					@SuppressWarnings("unchecked")
					Map<String, String> data = mapper.readValue(responseData.getContent(), Map.class);
					status = data.get("status");
					System.out.println(String.format("Got REST status: %s", status));
				} finally {
					response.close();
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (!status.equals("finished"));
		HttpGet httpGet = new HttpGet(String.format("http://0.0.0.0/v1/queue/%s/result", jobId));
		String result = null;
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			try {

				HttpEntity responseData = response.getEntity();
				Reader reader = new BufferedReader(new InputStreamReader(responseData.getContent()));
				for (int i = 0; i < 14; ++i) {
					reader.read();
				}
				StringBuilder stringBldr = new StringBuilder();
				int nextChar = 0;
				while (nextChar != -1) {
					nextChar = reader.read();
					if (nextChar != -1) {
						stringBldr.append((char) nextChar);
					}
				}
				reader.close();
				stringBldr.delete(stringBldr.length()-2, stringBldr.length());
				System.out.println(stringBldr.toString());
				stringBldr.trimToSize();
				cartesianLayout = new ByteArrayInputStream(stringBldr.toString().getBytes());

			} finally {
				response.close();
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cartesianLayout;
	}

}
