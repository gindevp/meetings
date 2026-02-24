import React from 'react';
import { Translate } from 'react-jhipster'; // eslint-disable-line

import MenuItem from 'app/shared/layout/menus/menu-item'; // eslint-disable-line

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/department">
        <Translate contentKey="global.menu.entities.department" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/room">
        <Translate contentKey="global.menu.entities.room" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/equipment">
        <Translate contentKey="global.menu.entities.equipment" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/room-equipment">
        <Translate contentKey="global.menu.entities.roomEquipment" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/meeting-type">
        <Translate contentKey="global.menu.entities.meetingType" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/meeting-level">
        <Translate contentKey="global.menu.entities.meetingLevel" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/meeting">
        <Translate contentKey="global.menu.entities.meeting" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/agenda-item">
        <Translate contentKey="global.menu.entities.agendaItem" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/meeting-participant">
        <Translate contentKey="global.menu.entities.meetingParticipant" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/meeting-task">
        <Translate contentKey="global.menu.entities.meetingTask" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/meeting-approval">
        <Translate contentKey="global.menu.entities.meetingApproval" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/meeting-document">
        <Translate contentKey="global.menu.entities.meetingDocument" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/incident">
        <Translate contentKey="global.menu.entities.incident" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
