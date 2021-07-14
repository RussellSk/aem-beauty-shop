package com.exadel.core.servlets;

import com.exadel.core.services.LikesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletName;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Slf4j
@Component(service = Servlet.class)
@SlingServletPaths(value = "/json/likes")
@SlingServletName(servletName = "LikesServlet")
public class LikesServlet extends SlingAllMethodsServlet {

    @Reference
    private LikesService likesService;

    /**
     * Handle POST request, process likes/dislikes
     * @param request SlingHttpServletRequest
     * @param response SlingHttpServletResponse
     * @throws ServletException throws servlet exception
     * @throws IOException throws IO exception
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        try {
            // Get request parameters
            String product = request.getParameter("product");
            boolean likeType = Boolean.parseBoolean(request.getParameter("like_type"));
            String userIpAddress = getUserIp(request);

            // Process like/dislike persistence
            if (likeType) {
                likesService.incrementLike(product,userIpAddress);
            } else {
                likesService.incrementDislike(product, userIpAddress);
            }

            // Get likes/dislikes counts
            int likesCount = likesService.getLikesCount(product);
            int dislikesCount = likesService.getDislikesCount(product);

            // Prepare response JSON
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", "success");
            jsonObject.put("likesCount", likesCount);
            jsonObject.put("dislikesCount", dislikesCount);

            // Write prepared JSON to response
            response.setContentType("application/json");
            response.getWriter().write(jsonObject.toString());

        } catch (Exception exception) {
            log.error("doPost: {}", getServletName(), exception);
            response.setContentType("text/html");
            response.getWriter().write("error" + exception.getMessage());
        }
    }

    /**
     * Helper function retrieves user ip address
     * @param request SlingHttpServletRequest
     * @return String user ip address
     */
    private String getUserIp(SlingHttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
