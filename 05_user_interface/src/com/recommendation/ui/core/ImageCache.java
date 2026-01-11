package com.recommendation.ui.core;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Image cache with LOCAL-FIRST loading strategy.
 * Moved to core package.
 */
public class ImageCache {

    private static final Map<String, ImageIcon> cache = Collections.synchronizedMap(new HashMap<>());
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static final String LOCAL_IMAGES_DIR = "../02_data_collection/images";

    private static final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder()
            .version(java.net.http.HttpClient.Version.HTTP_2)
            .followRedirects(java.net.http.HttpClient.Redirect.ALWAYS)
            .connectTimeout(java.time.Duration.ofSeconds(10))
            .build();

    public static void loadImage(String imageUrl, String productLink, int width, int height, boolean autoTrim,
            ImageCallback callback) {
        if (imageUrl == null || imageUrl.isEmpty())
            return;
        String cacheKey = imageUrl + "_" + width + "x" + height + (autoTrim ? "_trim" : "");
        if (cache.containsKey(cacheKey)) {
            callback.onImageLoaded(cache.get(cacheKey));
            return;
        }

        executor.submit(() -> {
            ImageIcon loadedIcon = tryLoadLocalImage(productLink, width, height, autoTrim);
            if (loadedIcon == null)
                loadedIcon = tryLoadFromHttp(imageUrl, width, height, autoTrim);

            final ImageIcon resultIcon = loadedIcon;
            if (resultIcon != null)
                cache.put(cacheKey, resultIcon);
            SwingUtilities.invokeLater(() -> callback.onImageLoaded(resultIcon));
        });
    }

    public static void loadImage(String imageUrl, String productLink, int width, int height, ImageCallback cb) {
        loadImage(imageUrl, productLink, width, height, false, cb);
    }

    private static ImageIcon tryLoadLocalImage(String productLink, int width, int height, boolean autoTrim) {
        if (productLink == null || productLink.isEmpty())
            return null;
        File localFile = new File(LOCAL_IMAGES_DIR, "prod_" + Math.abs(productLink.hashCode()) + ".jpg");
        if (localFile.exists()) {
            try {
                BufferedImage img = ImageIO.read(localFile);
                if (img != null) {
                    if (autoTrim)
                        img = trimBlackBorders(img);
                    return new ImageIcon(resizeToFit(img, width, height));
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    private static ImageIcon tryLoadFromHttp(String imageUrl, int width, int height, boolean autoTrim) {
        try {
            java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder()
                    .uri(new java.net.URI(imageUrl.replace(" ", "%20")))
                    .timeout(java.time.Duration.ofSeconds(15))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET().build();
            java.net.http.HttpResponse<byte[]> res = httpClient.send(req,
                    java.net.http.HttpResponse.BodyHandlers.ofByteArray());
            if (res.statusCode() == 200) {
                try (java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(res.body())) {
                    BufferedImage img = ImageIO.read(bais);
                    if (img != null) {
                        if (autoTrim)
                            img = trimBlackBorders(img);
                        return new ImageIcon(resizeToFit(img, width, height));
                    }
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    private static BufferedImage trimBlackBorders(BufferedImage img) {
        if (img == null)
            return null;
        int w = img.getWidth(), h = img.getHeight(), threshold = 25;
        int t = 0, b = h - 1, l = 0, r = w - 1;
        outerTop: for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                if (!isBlack(img.getRGB(x, y), threshold)) {
                    t = y;
                    break outerTop;
                }
        outerBottom: for (int y = h - 1; y >= 0; y--)
            for (int x = 0; x < w; x++)
                if (!isBlack(img.getRGB(x, y), threshold)) {
                    b = y;
                    break outerBottom;
                }
        outerLeft: for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++)
                if (!isBlack(img.getRGB(x, y), threshold)) {
                    l = x;
                    break outerLeft;
                }
        outerRight: for (int x = w - 1; x >= 0; x--)
            for (int y = 0; y < h; y++)
                if (!isBlack(img.getRGB(x, y), threshold)) {
                    r = x;
                    break outerRight;
                }
        if (l >= r || t >= b)
            return img;
        return img.getSubimage(l, t, r - l + 1, b - t + 1);
    }

    private static boolean isBlack(int rgb, int threshold) {
        int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
        return r < threshold && g < threshold && b < threshold;
    }

    private static BufferedImage resizeToFit(BufferedImage source, int tw, int th) {
        double scale = Math.min((double) tw / source.getWidth(), (double) th / source.getHeight());
        int sw = (int) Math.round(source.getWidth() * scale), sh = (int) Math.round(source.getHeight() * scale);
        BufferedImage res = new BufferedImage(tw, th, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = res.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(source, (tw - sw) / 2, (th - sh) / 2, sw, sh, null);
        g.dispose();
        return res;
    }

    public interface ImageCallback {
        void onImageLoaded(ImageIcon icon);
    }
}
