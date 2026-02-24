import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntities as getMeetings } from 'app/entities/meeting/meeting.reducer';
import { TaskType } from 'app/shared/model/enumerations/task-type.model';
import { TaskStatus } from 'app/shared/model/enumerations/task-status.model';
import { createEntity, getEntity, reset, updateEntity } from './meeting-task.reducer';

export const MeetingTaskUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const meetings = useAppSelector(state => state.meeting.entities);
  const meetingTaskEntity = useAppSelector(state => state.meetingTask.entity);
  const loading = useAppSelector(state => state.meetingTask.loading);
  const updating = useAppSelector(state => state.meetingTask.updating);
  const updateSuccess = useAppSelector(state => state.meetingTask.updateSuccess);
  const taskTypeValues = Object.keys(TaskType);
  const taskStatusValues = Object.keys(TaskStatus);

  const handleClose = () => {
    navigate('/meeting-task');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getMeetings({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.dueAt = convertDateTimeToServer(values.dueAt);
    if (values.remindBeforeMinutes !== undefined && typeof values.remindBeforeMinutes !== 'number') {
      values.remindBeforeMinutes = Number(values.remindBeforeMinutes);
    }

    const entity = {
      ...meetingTaskEntity,
      ...values,
      assignee: users.find(it => it.id.toString() === values.assignee?.toString()),
      assignedBy: users.find(it => it.id.toString() === values.assignedBy?.toString()),
      meeting: meetings.find(it => it.id.toString() === values.meeting?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          dueAt: displayDefaultDateTime(),
        }
      : {
          type: 'PRE_MEETING',
          status: 'TODO',
          ...meetingTaskEntity,
          dueAt: convertDateTimeFromServer(meetingTaskEntity.dueAt),
          assignee: meetingTaskEntity?.assignee?.id,
          assignedBy: meetingTaskEntity?.assignedBy?.id,
          meeting: meetingTaskEntity?.meeting?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="meetingsApp.meetingTask.home.createOrEditLabel" data-cy="MeetingTaskCreateUpdateHeading">
            <Translate contentKey="meetingsApp.meetingTask.home.createOrEditLabel">Create or edit a MeetingTask</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="meeting-task-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('meetingsApp.meetingTask.type')}
                id="meeting-task-type"
                name="type"
                data-cy="type"
                type="select"
              >
                {taskTypeValues.map(taskType => (
                  <option value={taskType} key={taskType}>
                    {translate(`meetingsApp.TaskType.${taskType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('meetingsApp.meetingTask.title')}
                id="meeting-task-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('meetingsApp.meetingTask.description')}
                id="meeting-task-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <ValidatedField
                label={translate('meetingsApp.meetingTask.dueAt')}
                id="meeting-task-dueAt"
                name="dueAt"
                data-cy="dueAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('meetingsApp.meetingTask.status')}
                id="meeting-task-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {taskStatusValues.map(taskStatus => (
                  <option value={taskStatus} key={taskStatus}>
                    {translate(`meetingsApp.TaskStatus.${taskStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('meetingsApp.meetingTask.remindBeforeMinutes')}
                id="meeting-task-remindBeforeMinutes"
                name="remindBeforeMinutes"
                data-cy="remindBeforeMinutes"
                type="text"
              />
              <ValidatedField
                id="meeting-task-assignee"
                name="assignee"
                data-cy="assignee"
                label={translate('meetingsApp.meetingTask.assignee')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="meeting-task-assignedBy"
                name="assignedBy"
                data-cy="assignedBy"
                label={translate('meetingsApp.meetingTask.assignedBy')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="meeting-task-meeting"
                name="meeting"
                data-cy="meeting"
                label={translate('meetingsApp.meetingTask.meeting')}
                type="select"
                required
              >
                <option value="" key="0" />
                {meetings
                  ? meetings.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/meeting-task" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default MeetingTaskUpdate;
