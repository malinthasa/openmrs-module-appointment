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
package org.openmrs.module.appointment.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointment.Appointment;
import org.openmrs.module.appointment.AppointmentBlock;
import org.openmrs.module.appointment.AppointmentType;
import org.openmrs.module.appointment.TimeSlot;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tests Time Slot methods in the {@link $ AppointmentService}}.
 */
public class TimeSlotServiceTest extends BaseModuleContextSensitiveTest {
	
	private AppointmentService service;
	
	private Integer amountOfTimeSlots = 4;
	
	@Before
	public void before() throws Exception {
		service = Context.getService(AppointmentService.class);
		executeDataSet("standardAppointmentTestDataset.xml");
	}
	
	@Test
	@Verifies(value = "should get all time slots", method = "getAllTimeSlots()")
	public void getAllTimeSlots_shouldGetAllTimeSlots() {
		List<TimeSlot> timeSlots = service.getAllTimeSlots();
		Assert.assertEquals(amountOfTimeSlots, (Integer) timeSlots.size());
	}
	
	@Test
	@Verifies(value = "should get all time slots by a given bool whether to include voided", method = "getAllTimeSlots(boolean)")
	public void getAllTimeSlots_shouldGetAllUnvoidedTimeSlots() {
		List<TimeSlot> timeSlots = service.getAllTimeSlots(false);
		Assert.assertEquals(3, timeSlots.size());
	}
	
	@Test
	@Verifies(value = "should get all time slots including voided", method = "getAllTimeSlots(boolean)")
	public void getAllTimeSlots_shouldGetAllIncludingVoidedTimeSlots() {
		List<TimeSlot> timeSlots = service.getAllTimeSlots(true);
		Assert.assertEquals(amountOfTimeSlots, (Integer) timeSlots.size());
	}
	
	@Test
	@Verifies(value = "should save new time slot", method = "saveTimeSlot(TimeSlot)")
	public void saveTimeSlot_shouldSaveNewTimeSlot() {
		
		AppointmentBlock appointmentBlock = service.getAppointmentBlock(1);
		TimeSlot timeSlot = new TimeSlot(appointmentBlock, new Date(), new Date());
		timeSlot = service.saveTimeSlot(timeSlot);
		List<TimeSlot> timeSlots = service.getAllTimeSlots();
		
		Assert.assertNotNull(timeSlot);
		Assert.assertEquals(amountOfTimeSlots + 1, timeSlots.size());
	}
	
	@Test
	@Verifies(value = "should save edited time slot without adding a new row", method = "saveTimeSlot(TimeSlot)")
	public void saveTimeSlot_shouldSaveEditedTimeSlot() {
		TimeSlot timeSlot = service.getTimeSlot(1);
		Date currentDate = new Date();
		timeSlot.setEndDate(currentDate);
		timeSlot = service.saveTimeSlot(timeSlot);
		Assert.assertNotNull(timeSlot);
		
		timeSlot = service.getTimeSlot(1);
		Assert.assertEquals(currentDate, timeSlot.getEndDate());
	}
	
	@Test
	@Verifies(value = "get correct time slot by a given id", method = "getTimeSlot(Integer timeSlotId)")
	public void getTimeSlot_shouldGetCorrectTimeSlot() {
		TimeSlot timeSlot = service.getTimeSlot(1);
		Date startDate = timeSlot.getStartDate();
		assertNotNull(timeSlot);
		assertEquals("2006-01-01 00:00:00.0", startDate.toString());
		
		timeSlot = service.getTimeSlot(2);
		startDate = timeSlot.getStartDate();
		assertNotNull(timeSlot);
		assertEquals("2006-01-01 00:00:00.1", startDate.toString());
		
		timeSlot = service.getTimeSlot(3);
		startDate = timeSlot.getStartDate();
		assertNotNull(timeSlot);
		assertEquals("2007-01-01 00:00:00.2", startDate.toString());
		
		timeSlot = service.getTimeSlot(5);
		Assert.assertNull(timeSlot);
	}
	
	@Test
	@Verifies(value = "get correct time slot by a given uuid", method = "getTimeSlotByUuid(String)")
	public void getTimeSlotByUuid_shouldgetCorrectTimeSlot() {
		TimeSlot timeSlot = service.getTimeSlotByUuid("c0c579b0-8e59-401d-8a4a-976a0b183604");
		Date startDate = timeSlot.getStartDate();
		assertNotNull(timeSlot);
		assertEquals("2006-01-01 00:00:00.0", startDate.toString());
		
		timeSlot = service.getTimeSlotByUuid("c0c579b0-8e59-401d-8a4a-976a0b183605");
		startDate = timeSlot.getStartDate();
		assertNotNull(timeSlot);
		assertEquals("2006-01-01 00:00:00.1", startDate.toString());
		
		timeSlot = service.getTimeSlotByUuid("c0c579b0-8e59-401d-8a4a-976a0b183606");
		startDate = timeSlot.getStartDate();
		assertNotNull(timeSlot);
		assertEquals("2007-01-01 00:00:00.2", startDate.toString());
		
		timeSlot = service.getTimeSlotByUuid("NOT A UUID");
		Assert.assertNull(timeSlot);
	}
	
	@Test
	@Verifies(value = "should void a time slot", method = "voidTimeSlot(TimeSlot, String)")
	public void voidTimeSlot_shouldVoidTimeSlot() {
		TimeSlot timeSlot = service.getTimeSlot(1);
		assertNotNull(timeSlot);
		assertFalse(timeSlot.isVoided());
		
		service.voidTimeSlot(timeSlot, "void reason");
		
		timeSlot = service.getTimeSlot(1);
		assertNotNull(timeSlot);
		assertTrue(timeSlot.isVoided());
		
		assertEquals(amountOfTimeSlots, (Integer) service.getAllTimeSlots().size());
	}
	
	@Test
	@Verifies(value = "should unvoid a time slot", method = "unvoidTimeSlot(TimeSlot)")
	public void unvoidTimeSlot_shouldUnvoidTimeSlot() {
		TimeSlot timeSlot = service.getTimeSlot(3);
		assertNotNull(timeSlot);
		assertTrue(timeSlot.isVoided());
		assertEquals("void reason", timeSlot.getVoidReason());
		
		service.unvoidTimeSlot(timeSlot);
		
		timeSlot = service.getTimeSlot(3);
		assertNotNull(timeSlot);
		assertFalse(timeSlot.isVoided());
		assertNull(timeSlot.getVoidReason());
		
		assertEquals(amountOfTimeSlots, (Integer) service.getAllTimeSlots().size());
	}
	
	@Test
	@Verifies(value = "should delete a given time slot", method = "purgeTimeSlot(TimeSlot)")
	public void purgeTimeSlot_shouldDeleteTimeSlot() {
		TimeSlot timeSlot = service.getTimeSlot(4);
		assertNotNull(timeSlot);
		
		service.purgeTimeSlot(timeSlot);
		
		timeSlot = service.getTimeSlot(4);
		assertNull(timeSlot);
		
		assertEquals(amountOfTimeSlots - 1, service.getAllTimeSlots().size());
	}
	
	@Test
	@Verifies(value = "retrieve all appointments scheduled in a given time slot", method = "getAppointmentsInTimeSlot(TimeSlot)")
	public void getAppointmentsInTimeSlot_shouldGetCorrectAppointments() {
		TimeSlot timeSlot = service.getTimeSlot(1);
		assertNotNull(timeSlot);
		
		List<Appointment> appointments = service.getAppointmentsInTimeSlot(timeSlot);
		assertNotNull(appointments);
		assertEquals(2, appointments.size());
		
		timeSlot = service.getTimeSlot(3);
		assertNotNull(timeSlot);
		appointments = service.getAppointmentsInTimeSlot(timeSlot);
		assertNotNull(appointments);
		assertEquals(1, appointments.size());
		
	}
	
	@Test
	@Verifies(value = "should return correct time slots", method = "getTimeSlotsByConstraints(AppointmentType, Date, Date, Provider)")
	public void getTimeSlotsByConstraints_shouldReturnCorrectTimeSlots() {
		//		AppointmentType appointmentType = service.getAppointmentType(1);
		//		assertNotNull(appointmentType);
		//		Date fromDate = new Date("2013-01-01 00:00:00.0");
		//		Date toDate = new Date("2006-01-01 00:00:00.0");
		//		//Should be null because fromDate>toDate
		//		Assert.assertNull(service.getTimeSlotsByConstraints(appointmentType, fromDate, toDate, null));
		//		
		//		fromDate = toDate;
		//		toDate = new Date("2013-01-01 00:00:00.1");
		//		//Should be null because appointment type is not optional
		//		Assert.assertNull(service.getTimeSlotsByConstraints(null, fromDate, toDate, null));
		//		
		//		List<TimeSlot> timeSlots = service.getTimeSlotsByConstraints(null, fromDate, toDate, Context.getProviderService().getProvider(2));
		//		assertEquals(0, timeSlots.size());
	}
	
	@Test
	@Verifies(value = "should return correct time slots", method = "getTimeSlotsInAppointmentBlock(AppointmentBlock)")
	public void getTimeSlotsInAppointmentBlock_shouldReturnCorrectTimeSlots() {
		AppointmentBlock appointmentBlock = service.getAppointmentBlock(1);
		assertNotNull(appointmentBlock);
		
		List<TimeSlot> timeSlots = service.getTimeSlotsInAppointmentBlock(appointmentBlock);
		assertEquals(4, timeSlots.size());
		
		//Should be empty list because appointment block is not exists
		timeSlots = service.getTimeSlotsInAppointmentBlock(null);
		assertEquals(0, timeSlots.size());
		
		//Should get an empty list because there are no time slots associated with the given appointment block
		appointmentBlock = service.getAppointmentBlock(2);
		timeSlots = service.getTimeSlotsInAppointmentBlock(appointmentBlock);
		assertEquals(0, timeSlots.size());
	}
}
