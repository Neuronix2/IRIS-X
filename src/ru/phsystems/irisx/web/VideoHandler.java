package ru.phsystems.irisx.web;

/**
 * Created with IntelliJ IDEA.
 * Author: Nikolay A. Viguro
 * Date: 09.09.12
 * Time: 17:45
 * License: GPL v3
 *
 *    Этот класс является proxy для mjpeg потока с камер
 *
 */

import lombok.extern.slf4j.Slf4j;
import ru.phsystems.irisx.Iris;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

@Slf4j

public class VideoHandler extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String shutdown = request.getParameter("shutdown");

        if (shutdown != null) {

            log.info("[cams] Shutdowning all cams");

            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);

            Iris.shutdownCams = true;

            response.getWriter().println("done");
        } else {

            Iris.shutdownCams = false;

            // правильный boundary - самое главное //
            response.setContentType("multipart/x-mixed-replace; boundary=--video boundary--");
            response.setStatus(HttpServletResponse.SC_OK);
            /////////////////////////////////////////

            log.info("[cam " + request.getParameter("cam") + "] Client connected");

            // URL = http://host/control/video?cam=<number>
            URL camURL = new URL("http://192.168.10." + request.getParameter("cam") + "/video.cgi");
            URLConnection uc = camURL.openConnection();

            OutputStream out = new BufferedOutputStream(response.getOutputStream());
            InputStream in = uc.getInputStream();

            byte[] bytes = new byte[4096];
            int bytesRead;

            while ((bytesRead = in.read(bytes)) != -1) {
                out.write(bytes, 0, bytesRead);
            }

            /*  TODO Very slow!
            MjpegFrame frame = null;
            MjpegInputStream inMJPEG = new MjpegInputStream(new BufferedInputStream(uc.getInputStream()));

           while ((frame = inMJPEG.readMjpegFrame()) != null) {

                if (Iris.shutdownCams == true) break;

                try {
                    // Определяем, есть ли лицо в фрейме
                    //byte[] faceImg = face.detect(frame.getJpegBytes());

                    // Заменяем фрейм новым изображением
                    //frame.setJpegBytes(faceImg);

                    output.write(frame.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/

        }

    }
}







