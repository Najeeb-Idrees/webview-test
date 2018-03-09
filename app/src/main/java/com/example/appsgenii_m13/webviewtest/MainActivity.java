package com.example.appsgenii_m13.webviewtest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{
	private WebView webView;

	private String url = "https://about.gitlab.com/";

	private HashMap<String, String> hashMap = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		webView = findViewById(R.id.web_view);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient()
		{
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
			{
				Toast.makeText(MainActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
			}
		});


		//		webView.loadUrl();


		new AsyncTask<Void, Void, Void>()
		{
			Document document = null;

			@Override
			protected Void doInBackground(Void... voids)
			{
				try
				{
					document = Jsoup.connect(url).get();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid)
			{
				super.onPostExecute(aVoid);

				Elements headers = document.body().getElementsByTag("header");
				Element header = headers.get(0);

				Elements navs = header.getElementsByTag("nav");
				Elements ul = navs.get(0).getElementsByTag("ul");

				Elements li = ul.get(0).getElementsByTag("li");

				for (Element element : li)
				{
					Element anch = element.select("a").first();

					if (anch != null)
					{
						String url = anch.attr("href");
						String text = anch.text();

						hashMap.put(text, url);

						Log.i("url", url);
					}

				}


				Log.i("nav", ul.toString());

				document.getElementsByClass("navbar navbar-fixed-top navbar-default").remove();
				document.getElementsByClass("animated text-center source-link").remove();
				document.body().attr("style", "padding-top:0px;");

				//mWebView.loadData(document.toString(),"text/html","utf-8");
				webView.loadDataWithBaseURL(url, document.toString(), "text/html", "utf-8", "");

				findViewById(R.id.menu).setVisibility(View.VISIBLE);
			}
		}.execute();


		findViewById(R.id.menu).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				for (Map.Entry<String, String> entry : hashMap.entrySet())
				{
					String key = entry.getKey();
					String value = entry.getValue();
					Log.i("text", key);
					Log.i("url", value);
				}

				Toast.makeText(MainActivity.this, "See console for urls and text", Toast.LENGTH_LONG).show();
			}
		});
	}
}
