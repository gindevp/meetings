import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AgendaItem from './agenda-item';
import AgendaItemDetail from './agenda-item-detail';
import AgendaItemUpdate from './agenda-item-update';
import AgendaItemDeleteDialog from './agenda-item-delete-dialog';

const AgendaItemRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AgendaItem />} />
    <Route path="new" element={<AgendaItemUpdate />} />
    <Route path=":id">
      <Route index element={<AgendaItemDetail />} />
      <Route path="edit" element={<AgendaItemUpdate />} />
      <Route path="delete" element={<AgendaItemDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AgendaItemRoutes;
