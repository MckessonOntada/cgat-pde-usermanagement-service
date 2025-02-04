package com.mckesson.cgat.pde.usermanagement.config;

import java.io.IOException;

import com.mckesson.cgat.pde.usermanagement.model.LoggedInUserDetails;
import com.mckesson.cgat.pde.usermanagement.repository.PracticeRepository;
import com.mckesson.cgat.pde.usermanagement.utils.CustomUtils;
import com.mckesson.cgat.pde.usermanagement.utils.UserSecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.mckesson.cgat.pde.usermanagement.exception.custom.UnauthorizedAccessException;
import com.mckesson.cgat.pde.usermanagement.utils.OrganizationContext;

import static com.mckesson.cgat.pde.usermanagement.config.TenantBaseFilter.extractTenantIdFromRequest;

@Slf4j
@Component
public class PracticeFilter extends CGATBaseFilter {

	@Autowired
	private PracticeRepository practiceRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			/*If a user passes the tenant base filter, it means they are either a system admin or a valid organization user.
			Here, we are checking whether the user's organization is active or not.*/
			Long organisationId = UserSecurityUtils.getLoggedInUser().getPracticeId();

			Boolean isPracticeActive = practiceRepository.findIsActiveById(organisationId).orElse(Boolean.TRUE);

			if (Boolean.FALSE.equals(isPracticeActive)) {
				throw new UnauthorizedAccessException(CustomUtils.UNAUTHORIZED_MESSAGE);
			}

			filterChain.doFilter(request, response);
		}
		catch (Exception e) {
			log.error("Error occurred in PracticeFilter. Message: {}", e.getMessage(), e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}


}
