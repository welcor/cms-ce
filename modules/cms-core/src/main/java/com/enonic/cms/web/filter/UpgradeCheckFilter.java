package com.enonic.cms.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.enonic.cms.upgrade.UpgradeChecker;
import com.enonic.cms.upgrade.UpgradeService;

@Component
public final class UpgradeCheckFilter
    extends OncePerRequestFilter
{
    private UpgradeChecker checker;

    @Override
    protected void doFilterInternal(final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain)
        throws ServletException, IOException
    {
        if (needUpgradeCheck(req) && this.checker.checkUpgrade(res)) {
            return;
        }

        chain.doFilter(req, res);
    }

    private boolean needUpgradeCheck(final HttpServletRequest req)
    {
        final String path = req.getRequestURI();
        return (path.contains("/site/") || path.contains("/admin/") || path.contains("/dav/"));
    }

    @Autowired
    public void setUpgradeService(final UpgradeService service)
    {
        this.checker = new UpgradeChecker(service);
    }
}
