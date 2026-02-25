INSERT INTO room (id, code, name, location, capacity, active) VALUES
(1, 'BRD-01', 'Boardroom', 'HQ - Floor 12', 18, 1),
(2, 'CONF-02', 'Conference A', 'HQ - Floor 6', 12, 1),
(3, 'CONF-03', 'Conference B', 'HQ - Floor 6', 10, 1),
(4, 'TRN-01', 'Training Room', 'HQ - Floor 3', 30, 1),
(5, 'HUD-01', 'Huddle Room 1', 'HQ - Floor 5', 6, 1),
(6, 'CONF-OLD', 'Old Meeting Room', 'HQ - Basement', 8, 0);

INSERT INTO department (id, code, name, description) VALUES
(1, 'HR',  'Human Resources', 'Talent acquisition, people operations, internal policies'),
(2, 'IT',  'Information Technology', 'Infrastructure, applications, security operations'),
(3, 'FIN', 'Finance', 'Budgeting, accounting, procurement controls'),
(4, 'OPS', 'Operations', 'Facilities, logistics, service delivery'),
(5, 'RND', 'R&D', 'Product research, engineering, innovation programs');

INSERT INTO equipment (id, code, name, description) VALUES
(1, 'EQ-PROJ', 'Projector', 'HD projector with HDMI input'),
(2, 'EQ-WB', 'Whiteboard', 'Magnetic whiteboard with markers & eraser'),
(3, 'EQ-VC', 'Video Conference Kit', 'Camera + mic + speaker (Teams/Zoom ready)'),
(4, 'EQ-MIC', 'Wireless Microphone', 'Handheld wireless mic set'),
(5, 'EQ-SPK', 'Speakerphone', 'Conference speakerphone with noise reduction'),
(6, 'EQ-TV', '65-inch TV', 'Large display for presentations'),
(7, 'EQ-ADPT', 'USB-C Adapter', 'USB-C to HDMI/VGA multiport adapter'),
(8, 'EQ-EXT', 'Extension Cords', 'Power strips & extension cords');

INSERT INTO meeting_level (id, name, description) VALUES
(1, 'CORPORATE', 'Company-wide or executive-level meetings involving multiple departments or strategic decisions'),
(2, 'DEPARTMENT', 'Internal meetings within a single department for operational alignment and execution');

INSERT INTO meeting_type (id, name, description) VALUES
(1, 'Weekly Sync', 'Weekly team alignment on priorities and blockers'),
(2, 'Project Steering Committee', 'Decision-making meeting for major project milestones'),
(3, 'Incident Review', 'Post-incident review and corrective actions'),
(4, 'Town Hall', 'Company-wide updates and Q&A'),
(5, 'Budget Review', 'Monthly budget and spend tracking review');

INSERT INTO room_equipment (id, quantity, room_id, equipment_id) VALUES
(1,  1, 1, 6),
(2,  1, 1, 3),
(3,  1, 1, 2),
(4,  1, 2, 1),
(5,  1, 2, 2),
(6,  1, 2, 5),
(7,  1, 3, 1),
(8,  1, 3, 2),
(9,  2, 4, 4),
(10, 1, 4, 6),
(11, 4, 4, 8),
(12, 2, 5, 7);


INSERT INTO meeting
(id, title, start_time, end_time, mode, online_link, objectives, note, status,
 created_at, submitted_at, approved_at, canceled_at,
 type_id, level_id, organizer_department_id, room_id, requester_id, host_id)
VALUES
(1, 'IT Weekly Sync - Platform Team',
 '2026-02-10 02:00:00.000000', '2026-02-10 03:00:00.000000', 'HYBRID',
 'https://teams.microsoft.com/l/meetup-join/it-weekly-20260210',
 'Align on sprint goals, review incidents, unblock dependencies.',
 'Bring updates on infra migration progress.',
 'COMPLETED',
 '2026-02-03 09:20:00.000000', '2026-02-03 10:00:00.000000', '2026-02-04 04:15:00.000000', NULL,
 1, 2, 2, 2, 1, 2),

(2, 'Budget Review - Feb 2026 (Finance & Ops)',
 '2026-02-18 07:30:00.000000', '2026-02-18 09:00:00.000000', 'IN_PERSON',
 NULL,
 'Review Feb spend, forecast Q2 needs, approve procurement exceptions.',
 'Procurement exceptions need justification attached.',
 'APPROVED',
 '2026-02-01 06:00:00.000000', '2026-02-02 03:00:00.000000', '2026-02-03 02:30:00.000000', NULL,
 5, 2, 3, 1, 1, 1),

(3, 'R&D Project Phoenix - Steering Committee',
 '2026-03-04 03:00:00.000000', '2026-03-04 04:30:00.000000', 'HYBRID',
 'https://zoom.us/j/phoenix-steering-20260304',
 'Decide MVP scope, confirm timeline, approve staffing plan.',
 'Decision points: MVP features list, QA timeline, vendor contract.',
 'PENDING_APPROVAL',
 '2026-02-20 05:10:00.000000', '2026-02-22 02:00:00.000000', NULL, NULL,
 2, 1, 5, 1, 2, 2),

(4, 'Company Town Hall - Q1 Updates',
 '2026-03-11 02:00:00.000000', '2026-03-11 03:30:00.000000', 'ONLINE',
 'https://teams.microsoft.com/l/meetup-join/townhall-q1-2026',
 'Leadership updates, OKR progress, open Q&A.',
 'Collect Q&A questions via form by Mar 8.',
 'DRAFT',
 '2026-02-23 06:45:00.000000', NULL, NULL, NULL,
 4, 1, 1, NULL, 1, 1),

(5, 'Incident Review - VPN Outage (Feb 21)',
 '2026-02-23 03:00:00.000000', '2026-02-23 04:00:00.000000', 'ONLINE',
 'https://zoom.us/j/incident-review-vpn-20260223',
 'Root cause analysis, impact review, corrective actions.',
 'Attach timeline, logs summary, and comms draft.',
 'COMPLETED',
 '2026-02-21 07:10:00.000000', '2026-02-21 08:00:00.000000', '2026-02-21 12:20:00.000000', NULL,
 3, 1, 2, NULL, 2, 2),

(6, 'HR Policy Update - Remote Work Addendum',
 '2026-02-28 06:30:00.000000', '2026-02-28 07:30:00.000000', 'HYBRID',
 'https://teams.microsoft.com/l/meetup-join/hr-policy-20260228',
 'Walk through policy changes and answer questions.',
 'Legal review already completed; focus on rollout plan.',
 'APPROVED',
 '2026-02-10 06:00:00.000000', '2026-02-11 02:15:00.000000', '2026-02-12 05:05:00.000000', NULL,
 1, 2, 1, 4, 1, 1),

(7, 'Ops Facilities - Vendor Kickoff (Catering)',
 '2026-02-26 04:00:00.000000', '2026-02-26 04:45:00.000000', 'IN_PERSON',
 NULL,
 'Kickoff with catering vendor, finalize SLA and menu rotation.',
 'Bring feedback from last quarter.',
 'CANCELED',
 '2026-02-15 03:30:00.000000', '2026-02-15 04:00:00.000000', '2026-02-16 02:10:00.000000', '2026-02-24 01:00:00.000000',
 2, 2, 4, 3, 2, 2),

(8, 'R&D Demo Day - Sprint 8',
 '2026-03-01 08:30:00.000000', '2026-03-01 10:00:00.000000', 'HYBRID',
 'https://zoom.us/j/rnd-demo-day-s8-20260301',
 'Demo new features, collect stakeholder feedback, confirm next sprint.',
 'Please keep demos under 10 minutes each.',
 'APPROVED',
 '2026-02-12 07:00:00.000000', '2026-02-13 02:00:00.000000', '2026-02-14 03:30:00.000000', NULL,
 1, 1, 5, 2, 2, 2),

(9, 'Security Exception Request - Legacy Server',
 '2026-02-25 03:00:00.000000', '2026-02-25 03:30:00.000000', 'ONLINE',
 'https://teams.microsoft.com/l/meetup-join/sec-exception-20260225',
 'Review exception request, decide compensating controls.',
 'Prepare risk assessment and mitigation plan.',
 'REJECTED',
 '2026-02-19 05:00:00.000000', '2026-02-19 06:10:00.000000', NULL, NULL,
 2, 1, 2, NULL, 1, 2),

(10, 'IT Change Advisory Board (CAB) - Week 9',
 '2026-02-27 09:00:00.000000', '2026-02-27 10:00:00.000000', 'HYBRID',
 'https://teams.microsoft.com/l/meetup-join/cab-week9-2026',
 'Review and approve planned changes for next week.',
 'Changes without rollback plan will be deferred.',
 'APPROVED',
 '2026-02-18 02:20:00.000000', '2026-02-18 03:00:00.000000', '2026-02-19 01:30:00.000000', NULL,
 2, 2, 2, 5, 1, 1);


INSERT INTO agenda_item 
(id, item_order, topic, presenter_name, duration_minutes, note, meeting_id) VALUES
(1,1,'Sprint highlights & metrics','Linh Nguyen',15,NULL,1),
(2,2,'Infra migration status','Quang Pham',20,NULL,1),
(3,3,'Open incidents & on-call handover','Duc Ngo',15,'Review top 3 recurring alerts',1),
(4,4,'Risks & blockers','All',10,NULL,1),

(5,1,'Spend summary vs budget','Minh Tran',25,NULL,2),
(6,2,'Procurement exceptions','Hoa Nguyen',25,'List exceptions and owners',2),
(7,3,'Forecast for Q2','Ngoc Bui',30,NULL,2),
(8,4,'Decisions & action items','Minh Tran',10,NULL,2),

(9,1,'Progress since last steering','An Vo',15,NULL,3),
(10,2,'MVP scope decision','Khanh Pham',30,NULL,3),
(11,3,'Timeline & staffing plan','Quang Pham',20,NULL,3),
(12,4,'Risks, dependencies, and next steps','All',20,NULL,3),

(13,1,'CEO updates & strategy','Admin',25,NULL,4),
(14,2,'Q1 OKR progress','Thao Le',20,NULL,4),
(15,3,'People & culture highlights','HR Team',15,NULL,4),
(16,4,'Open Q&A','All',30,NULL,4),

(17,1,'Incident timeline recap','Duc Ngo',15,NULL,5),
(18,2,'Root cause analysis (RCA)','Quang Pham',20,NULL,5),
(19,3,'Customer/internal impact','Linh Nguyen',10,NULL,5),
(20,4,'Corrective and preventive actions (CAPA)','An Vo',15,NULL,5),

(21,1,'Policy changes overview','Thao Le',25,NULL,6),
(22,2,'Implementation and comms plan','Hoa Nguyen',15,NULL,6),
(23,3,'Q&A','All',20,NULL,6),

(24,1,'Vendor introductions','Hoa Nguyen',10,NULL,7),
(25,2,'SLA and service expectations','Minh Tran',15,NULL,7),
(26,3,'Menu rotation & feedback loop','Vendor Team',15,NULL,7),
(27,4,'Next steps','Hoa Nguyen',5,NULL,7),

(28,1,'Sprint 8 demo - Feature A','Khanh Pham',10,NULL,8),
(29,2,'Sprint 8 demo - Feature B','An Vo',10,NULL,8),
(30,3,'Sprint 8 demo - Platform improvements','Quang Pham',10,NULL,8),
(31,4,'Feedback & prioritization','Stakeholders',20,NULL,8),
(32,5,'Next sprint planning notes','An Vo',15,NULL,8),

(33,1,'Exception request summary','Linh Nguyen',10,NULL,9),
(34,2,'Risk review','Duc Ngo',10,NULL,9),
(35,3,'Decision & required mitigations','Security Panel',10,NULL,9),

(36,1,'Review change requests','Quang Pham',30,NULL,10),
(37,2,'High-risk changes discussion','Duc Ngo',15,NULL,10),
(38,3,'Approvals and scheduling','All',15,NULL,10);


INSERT INTO meeting_participant
(id, role, is_required, attendance, absent_reason, user_id, meeting_id) VALUES
(1,'HOST',1,'PRESENT',NULL,2,1),
(2,'MAIN',1,'PRESENT',NULL,1,1),
(3,'ATTENDEE',1,'PRESENT',NULL,2,1),
(4,'ATTENDEE',0,'EXCUSED','Client workshop',1,1),
(5,'SECRETARY',1,'PRESENT',NULL,1,1),

(6,'HOST',1,'PRESENT',NULL,2,2),
(7,'MAIN',1,'PRESENT',NULL,2,2),
(8,'ATTENDEE',1,'PRESENT',NULL,1,2),
(9,'ATTENDEE',0,'ABSENT','Travel',2,2),
(10,'SECRETARY',1,'PRESENT',NULL,1,2),

(11,'HOST',1,'NOT_MARKED',NULL,2,3),
(12,'MAIN',1,'NOT_MARKED',NULL,2,3),
(13,'ATTENDEE',1,'NOT_MARKED',NULL,2,3),
(14,'ATTENDEE',0,'NOT_MARKED',NULL,2,3),
(15,'SECRETARY',1,'NOT_MARKED',NULL,1,3),

(16,'HOST',1,'NOT_MARKED',NULL,1,4),
(17,'MAIN',1,'NOT_MARKED',NULL,1,4),
(18,'ATTENDEE',0,'NOT_MARKED',NULL,1,4),
(19,'ATTENDEE',0,'NOT_MARKED',NULL,2,4),
(20,'SECRETARY',1,'NOT_MARKED',NULL,2,4),

(21,'HOST',1,'PRESENT',NULL,2,5),
(22,'MAIN',1,'PRESENT',NULL,2,5),
(23,'ATTENDEE',1,'PRESENT',NULL,1,5),
(24,'ATTENDEE',1,'PRESENT',NULL,2,5),
(25,'SECRETARY',1,'PRESENT',NULL,1,5),

(26,'HOST',1,'NOT_MARKED',NULL,1,6),
(27,'MAIN',1,'NOT_MARKED',NULL,2,6),
(28,'ATTENDEE',0,'NOT_MARKED',NULL,1,6),
(29,'ATTENDEE',0,'NOT_MARKED',NULL,2,6),
(30,'SECRETARY',1,'NOT_MARKED',NULL,1,6),

(31,'HOST',1,'NOT_MARKED',NULL,2,7),
(32,'MAIN',1,'NOT_MARKED',NULL,2,7),
(33,'ATTENDEE',0,'NOT_MARKED',NULL,1,7),
(34,'SECRETARY',1,'NOT_MARKED',NULL,1,7),

(35,'HOST',1,'NOT_MARKED',NULL,2,8),
(36,'MAIN',1,'NOT_MARKED',NULL,2,8),
(37,'ATTENDEE',1,'NOT_MARKED',NULL,2,8),
(38,'ATTENDEE',0,'NOT_MARKED',NULL,1,8),
(39,'SECRETARY',1,'NOT_MARKED',NULL,1,8),

(40,'HOST',1,'NOT_MARKED',NULL,2,9),
(41,'MAIN',1,'NOT_MARKED',NULL,1,9),
(42,'ATTENDEE',1,'NOT_MARKED',NULL,2,9),
(43,'SECRETARY',1,'NOT_MARKED',NULL,1,9),

(44,'HOST',1,'NOT_MARKED',NULL,2,10),
(45,'MAIN',1,'NOT_MARKED',NULL,2,10),
(46,'ATTENDEE',1,'NOT_MARKED',NULL,1,10),
(47,'ATTENDEE',0,'NOT_MARKED',NULL,2,10),
(48,'SECRETARY',1,'NOT_MARKED',NULL,1,10);


INSERT INTO meeting_task
(id, type, title, description, due_at, status, remind_before_minutes, assignee_id, assigned_by_id, meeting_id)
VALUES
(1,'PRE_MEETING','Prepare sprint highlights slide','Pull metrics from dashboard and prepare 1-slide summary.','2026-02-09 06:00:00.000000','DONE',60,1,2,1),
(2,'POST_MEETING','Publish minutes to Confluence','Upload minutes and tag action owners.','2026-02-10 10:00:00.000000','DONE',120,1,2,1),

(3,'PRE_MEETING','Collect procurement exception list','Gather all exceptions, owner, justification, amount.','2026-02-16 06:00:00.000000','DONE',180,2,2,2),
(4,'POST_MEETING','Update budget tracker & circulate decisions','Send summary and update shared tracker.','2026-02-19 04:00:00.000000','OVERDUE',60,1,2,2),

(5,'PRE_MEETING','Draft MVP scope options','Prepare 2 scope options with trade-offs.','2026-03-02 06:00:00.000000','TODO',1440,2,2,3),
(6,'PRE_MEETING','Prepare staffing plan proposal','List required roles and sourcing plan.','2026-03-02 08:00:00.000000','IN_PROGRESS',720,2,2,3),

(7,'PRE_MEETING','Collect Q&A questions','Publish form and consolidate questions.','2026-03-08 09:00:00.000000','TODO',2880,2,1,4),
(8,'PRE_MEETING','Create town hall deck outline','Agenda, key messages, metrics, announcements.','2026-03-07 06:00:00.000000','TODO',1440,1,1,4),

(9,'PRE_MEETING','Compile incident timeline','Prepare timeline including key events and comms.','2026-02-22 07:00:00.000000','DONE',180,2,2,5),
(10,'POST_MEETING','Implement VPN failover monitoring','Add synthetic checks and alert routing.','2026-02-28 07:00:00.000000','IN_PROGRESS',1440,1,2,5),
(11,'POST_MEETING','Update incident comms template','Improve comms template with clear ETA fields.','2026-03-01 07:00:00.000000','TODO',1440,1,2,5),

(12,'PRE_MEETING','Finalize rollout schedule','Phased rollout dates and owner list.','2026-02-27 06:00:00.000000','IN_PROGRESS',720,2,1,6),

(13,'PRE_MEETING','Confirm vendor attendee list','Get names, roles, and contact details.','2026-02-25 06:00:00.000000','TODO',120,1,2,7),

(14,'PRE_MEETING','Demo rehearsal','Ensure demo environment ready and stable.','2026-02-28 08:00:00.000000','TODO',720,2,2,8),
(15,'POST_MEETING','Consolidate feedback','Summarize feedback and convert to backlog items.','2026-03-03 07:00:00.000000','TODO',1440,2,2,8),

(16,'PRE_MEETING','Prepare risk assessment','Include CVE exposure and compensating controls.','2026-02-24 06:00:00.000000','IN_PROGRESS',180,1,2,9),

(17,'PRE_MEETING','Collect change requests list','Compile change requests with owner and rollback plan.','2026-02-26 06:00:00.000000','IN_PROGRESS',240,2,2,10),
(18,'POST_MEETING','Update change calendar','Publish approved schedule to shared calendar.','2026-02-28 08:00:00.000000','TODO',360,1,2,10);


INSERT INTO meeting_approval
(id, step, decision, reason, decided_at, meeting_id, decided_by_id)
VALUES
(1, 1, 'APPROVED', 'Standard weekly sync', STR_TO_DATE('2026-02-04T04:15:00Z','%Y-%m-%dT%H:%i:%sZ'), 1, 1),
(2, 1, 'APPROVED', 'Budget review scheduled per monthly cadence', STR_TO_DATE('2026-02-03T02:30:00Z','%Y-%m-%dT%H:%i:%sZ'), 2, 1),
(3, 1, 'APPROVED', 'Incident review required for critical outage', STR_TO_DATE('2026-02-21T12:20:00Z','%Y-%m-%dT%H:%i:%sZ'), 5, 1),
(4, 1, 'APPROVED', 'Policy update approved for rollout', STR_TO_DATE('2026-02-12T05:05:00Z','%Y-%m-%dT%H:%i:%sZ'), 6, 1),
(5, 1, 'APPROVED', 'Vendor kickoff approved', STR_TO_DATE('2026-02-16T02:10:00Z','%Y-%m-%dT%H:%i:%sZ'), 7, 1),
(6, 1, 'APPROVED', 'Sprint demo approved', STR_TO_DATE('2026-02-14T03:30:00Z','%Y-%m-%dT%H:%i:%sZ'), 8, 1),
(7, 1, 'REJECTED', 'Risk too high; mitigation insufficient', STR_TO_DATE('2026-02-20T02:40:00Z','%Y-%m-%dT%H:%i:%sZ'), 9, 1),
(8, 1, 'APPROVED', 'CAB is standard process', STR_TO_DATE('2026-02-19T01:30:00Z','%Y-%m-%dT%H:%i:%sZ'), 10, 1);

INSERT INTO meeting_document
(id, doc_type, file_name, content_type, file, file_content_type, uploaded_at, meeting_id, uploaded_by_id)
VALUES
(1, 'MINUTES',    'minutes_it_weekly_2026-02-10.docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', NULL, NULL, STR_TO_DATE('2026-02-10T10:15:00Z','%Y-%m-%dT%H:%i:%sZ'), 1, 1),
(2, 'ATTACHMENT', 'sprint_metrics_week6.pdf',          'application/pdf',                                                   NULL, NULL, STR_TO_DATE('2026-02-10T01:40:00Z','%Y-%m-%dT%H:%i:%sZ'), 1, 1),
(3, 'ATTACHMENT', 'budget_review_feb2026.xlsx',        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', NULL, NULL, STR_TO_DATE('2026-02-17T06:00:00Z','%Y-%m-%dT%H:%i:%sZ'), 2, 1),
(4, 'MINUTES',    'incident_review_vpn_outage_2026-02-23.docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', NULL, NULL, STR_TO_DATE('2026-02-23T05:10:00Z','%Y-%m-%dT%H:%i:%sZ'), 5, 1),
(5, 'ATTACHMENT', 'vpn_outage_timeline.md',            'text/markdown',                                                    NULL, NULL, STR_TO_DATE('2026-02-22T10:00:00Z','%Y-%m-%dT%H:%i:%sZ'), 5, 1),
(6, 'ATTACHMENT', 'remote_work_addendum_v3.pdf',       'application/pdf',                                                   NULL, NULL, STR_TO_DATE('2026-02-20T06:00:00Z','%Y-%m-%dT%H:%i:%sZ'), 6, 1),
(7, 'ATTACHMENT', 'demo_day_agenda_sprint8.pdf',       'application/pdf',                                                   NULL, NULL, STR_TO_DATE('2026-02-25T07:00:00Z','%Y-%m-%dT%H:%i:%sZ'), 8, 1),
(8, 'ATTACHMENT', 'cab_changes_week9.csv',             'text/csv',                                                          NULL, NULL, STR_TO_DATE('2026-02-24T06:30:00Z','%Y-%m-%dT%H:%i:%sZ'), 10, 1);


INSERT INTO incident
(id, title, description, reported_at, severity, status, meeting_id, reported_by_id)
VALUES
(1,
 'VPN Outage - Authentication Timeout',
 'VPN connections failed intermittently due to backend auth timeouts; impact to remote staff for ~45 minutes.',
 STR_TO_DATE('2026-02-21T06:55:00Z','%Y-%m-%dT%H:%i:%sZ'),
 'HIGH',
 'CLOSED',
 5,
 1
),
(2,
 'Recurring Alert Storm - Load Balancer Health Checks',
 'Health check misconfiguration caused frequent flapping and alert noise; impacted on-call response quality.',
 STR_TO_DATE('2026-02-08T04:00:00Z','%Y-%m-%dT%H:%i:%sZ'),
 'MEDIUM',
 'MITIGATED',
 1,
 1
);