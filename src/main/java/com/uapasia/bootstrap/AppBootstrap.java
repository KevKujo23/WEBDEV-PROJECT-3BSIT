package com.uapasia.bootstrap;

import com.uapasia.repo.ContextStore;
import javax.servlet.*;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppBootstrap implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent sce) {
        // Touch the stores so they exist
        ContextStore.users(sce.getServletContext());
        ContextStore.profs(sce.getServletContext());
        ContextStore.ratings(sce.getServletContext());
    }
}
