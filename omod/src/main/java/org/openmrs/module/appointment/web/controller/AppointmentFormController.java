/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.appointment.web.controller;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointment.Appointment;
import org.openmrs.module.appointment.AppointmentType;
import org.openmrs.module.appointment.TimeSlot;
import org.openmrs.module.appointment.api.AppointmentService;
import org.openmrs.module.appointment.validator.AppointmentValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for creating appointments.
 */
@Controller
public class AppointmentFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/appointment/appointmentForm", method = RequestMethod.GET)
	public void showForm(ModelMap model) {
		//default empty Object
		Set<AppointmentType> appointmentTypeList = new HashSet<AppointmentType>();
		List<Provider> providerList = new LinkedList<Provider>();
		
		//only fill the Object is the user has authenticated properly
		if (Context.isAuthenticated()) {
			AppointmentService appointmentService = Context.getService(AppointmentService.class);
			appointmentTypeList = appointmentService.getAllAppointmentTypes();
			providerList = Context.getProviderService().getAllProviders();
		}
		
		model.addAttribute("appointmentTypeList", appointmentTypeList);
		model.addAttribute("providerList", providerList);
	}
	
	@ModelAttribute("appointment")
	public Appointment getAppointment(@RequestParam(value = "appointmentId", required = false) Integer appointmentId) {
		Appointment appointment = null;
		
		if (Context.isAuthenticated()) {
			AppointmentService as = Context.getService(AppointmentService.class);
			if (appointmentId != null)
				appointment = as.getAppointment(appointmentId);
		}
		
		if (appointment == null)
			appointment = new Appointment();
		
		return appointment;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(HttpServletRequest request, Appointment appointment, BindingResult result) throws Exception {
		HttpSession httpSession = request.getSession();
		
		if (Context.isAuthenticated()) {
			AppointmentService appointmentService = Context.getService(AppointmentService.class);
			
			if (request.getParameter("save") != null) {
				new AppointmentValidator().validate(appointment, result);
			}
			if (result.hasErrors())
				return null;
			
			if (request.getParameter("findAvailableTime") != null) {
				return null;
			}
		}
		return "";
	}
	
	@RequestMapping(params = "findAvailableTime", method = RequestMethod.POST)
	public void onFindTimesClick(HttpServletRequest request, Appointment appointment, BindingResult result, ModelMap model)
	        throws Exception {
		if (Context.isAuthenticated()) {
			List<TimeSlot> availableTimes = Context.getService(AppointmentService.class).getAllTimeSlots();
			
			model.addAttribute("availableTimes", availableTimes);
		}
	}
	
}
