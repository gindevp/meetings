import department from 'app/entities/department/department.reducer';
import room from 'app/entities/room/room.reducer';
import equipment from 'app/entities/equipment/equipment.reducer';
import roomEquipment from 'app/entities/room-equipment/room-equipment.reducer';
import meetingType from 'app/entities/meeting-type/meeting-type.reducer';
import meetingLevel from 'app/entities/meeting-level/meeting-level.reducer';
import meeting from 'app/entities/meeting/meeting.reducer';
import agendaItem from 'app/entities/agenda-item/agenda-item.reducer';
import meetingParticipant from 'app/entities/meeting-participant/meeting-participant.reducer';
import meetingTask from 'app/entities/meeting-task/meeting-task.reducer';
import meetingApproval from 'app/entities/meeting-approval/meeting-approval.reducer';
import meetingDocument from 'app/entities/meeting-document/meeting-document.reducer';
import incident from 'app/entities/incident/incident.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  department,
  room,
  equipment,
  roomEquipment,
  meetingType,
  meetingLevel,
  meeting,
  agendaItem,
  meetingParticipant,
  meetingTask,
  meetingApproval,
  meetingDocument,
  incident,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
