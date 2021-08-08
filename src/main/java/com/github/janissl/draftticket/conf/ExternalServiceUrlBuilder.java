package com.github.janissl.draftticket.conf;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExternalServiceUrlBuilder {
    public static String buildUrl(String baseUrl, String extraPath) throws MalformedURLException {
        List<String> basePathSegments = new ArrayList<>(Arrays.asList(new URL(baseUrl).getPath().split("/")));

        if (null != extraPath) {
            List<String> extraPathSegments = new ArrayList<>(Arrays.asList(extraPath.split("/")));
            basePathSegments.addAll(extraPathSegments);
        }

        List<String> normalizedSegments =
                basePathSegments.stream()
                        .filter(x -> !x.isEmpty())
                        .collect(Collectors.toList());

        String newPath = String.join("/", normalizedSegments);
        return new URL(new URL(baseUrl.replaceAll("/+$", "")), newPath).toString();
    }
}
