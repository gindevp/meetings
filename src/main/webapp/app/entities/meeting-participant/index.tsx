import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import MeetingParticipant from './meeting-participant';
import MeetingParticipantDetail from './meeting-participant-detail';
import MeetingParticipantUpdate from './meeting-participant-update';
import MeetingParticipantDeleteDialog from './meeting-participant-delete-dialog';

const MeetingParticipantRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<MeetingParticipant />} />
    <Route path="new" element={<MeetingParticipantUpdate />} />
    <Route path=":id">
      <Route index element={<MeetingParticipantDetail />} />
      <Route path="edit" element={<MeetingParticipantUpdate />} />
      <Route path="delete" element={<MeetingParticipantDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MeetingParticipantRoutes;
