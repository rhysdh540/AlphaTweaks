package dev.rdh.alphatweaks;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SkinUtil {
	public static String getSkinDownloadUrl(String name) {
		if (name == null || name.isEmpty()) return null;
		String uuid = getUuid(name);
		if(uuid == null) return null;

		String textures = getTextures(name);
		if(textures == null) return null;

		String decoded = new String(Base64.getDecoder().decode(textures.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
		JsonObject json = JsonParser.parseString(decoded).getAsJsonObject();
		if(json.has("textures")) {
			JsonObject texturesJson = json.getAsJsonObject("textures");
			if(texturesJson.has("SKIN")) {
				JsonObject skinJson = texturesJson.getAsJsonObject("SKIN");
				if(skinJson.has("url")) {
					return skinJson.get("url").getAsString();
				}
			}
		}

		return null;
	}

	private static String getTextures(String name) {
		String uuid = getUuid(name);
		if(uuid == null) return null;

		try(InputStream in = get("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false")) {
			if(in == null) return null;

			JsonObject json = JsonParser.parseReader(new InputStreamReader(in)).getAsJsonObject();
			if (json.has("errorMessage")) {
				throw new RuntimeException(json.get("errorMessage").getAsString());
			}

			if(json.has("properties")) {
				for(var el : json.getAsJsonArray("properties")) {
					JsonObject prop = el.getAsJsonObject();
					if(prop.has("name") && prop.get("name").getAsString().equals("textures") && prop.has("value")) {
						return prop.get("value").getAsString();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static String getUuid(String name) {

		try(InputStream in = get("https://api.mojang.com/users/profiles/minecraft/" + name)) {
			if(in == null) return null;

			JsonObject json = JsonParser.parseReader(new InputStreamReader(in)).getAsJsonObject();
			if (json.has("errorMessage")) {
				throw new RuntimeException(json.get("errorMessage").getAsString());
			}

			if(json.has("id")) {
				return json.get("id").getAsString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static InputStream get(String url) {
		try {
			URI uri = URI.create(url);
			URL u = uri.toURL();
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.connect();

			return conn.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
