package edu.cnm.deepdive.coffeeshop.service;

import edu.cnm.deepdive.coffeeshop.model.entity.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@org.springframework.context.annotation.Profile("service")
public class ContextProfileServiceImpl implements ContextProfileService {

  @Override
  public Profile getContextProfile() {
    //noinspection DataFlowIssue
    return (Profile) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

}
