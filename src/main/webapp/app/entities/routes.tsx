import React from 'react';
import { Route } from 'react-router'; // eslint-disable-line

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Department from './department';
import Room from './room';
import Equipment from './equipment';
import RoomEquipment from './room-equipment';
import MeetingType from './meeting-type';
import MeetingLevel from './meeting-level';
import Meeting from './meeting';
import AgendaItem from './agenda-item';
import MeetingParticipant from './meeting-participant';
import MeetingTask from './meeting-task';
import MeetingApproval from './meeting-approval';
import MeetingDocument from './meeting-document';
import Incident from './incident';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="department/*" element={<Department />} />
        <Route path="room/*" element={<Room />} />
        <Route path="equipment/*" element={<Equipment />} />
        <Route path="room-equipment/*" element={<RoomEquipment />} />
        <Route path="meeting-type/*" element={<MeetingType />} />
        <Route path="meeting-level/*" element={<MeetingLevel />} />
        <Route path="meeting/*" element={<Meeting />} />
        <Route path="agenda-item/*" element={<AgendaItem />} />
        <Route path="meeting-participant/*" element={<MeetingParticipant />} />
        <Route path="meeting-task/*" element={<MeetingTask />} />
        <Route path="meeting-approval/*" element={<MeetingApproval />} />
        <Route path="meeting-document/*" element={<MeetingDocument />} />
        <Route path="incident/*" element={<Incident />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
