import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './agenda-item.reducer';

export const AgendaItem = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const agendaItemList = useAppSelector(state => state.agendaItem.entities);
  const loading = useAppSelector(state => state.agendaItem.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="agenda-item-heading" data-cy="AgendaItemHeading">
        <Translate contentKey="meetingsApp.agendaItem.home.title">Agenda Items</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="meetingsApp.agendaItem.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/agenda-item/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="meetingsApp.agendaItem.home.createLabel">Create new Agenda Item</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {agendaItemList && agendaItemList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="meetingsApp.agendaItem.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('itemOrder')}>
                  <Translate contentKey="meetingsApp.agendaItem.itemOrder">Item Order</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('itemOrder')} />
                </th>
                <th className="hand" onClick={sort('topic')}>
                  <Translate contentKey="meetingsApp.agendaItem.topic">Topic</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('topic')} />
                </th>
                <th className="hand" onClick={sort('presenterName')}>
                  <Translate contentKey="meetingsApp.agendaItem.presenterName">Presenter Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('presenterName')} />
                </th>
                <th className="hand" onClick={sort('durationMinutes')}>
                  <Translate contentKey="meetingsApp.agendaItem.durationMinutes">Duration Minutes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('durationMinutes')} />
                </th>
                <th className="hand" onClick={sort('note')}>
                  <Translate contentKey="meetingsApp.agendaItem.note">Note</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('note')} />
                </th>
                <th>
                  <Translate contentKey="meetingsApp.agendaItem.meeting">Meeting</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {agendaItemList.map((agendaItem, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/agenda-item/${agendaItem.id}`} color="link" size="sm">
                      {agendaItem.id}
                    </Button>
                  </td>
                  <td>{agendaItem.itemOrder}</td>
                  <td>{agendaItem.topic}</td>
                  <td>{agendaItem.presenterName}</td>
                  <td>{agendaItem.durationMinutes}</td>
                  <td>{agendaItem.note}</td>
                  <td>{agendaItem.meeting ? <Link to={`/meeting/${agendaItem.meeting.id}`}>{agendaItem.meeting.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/agenda-item/${agendaItem.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/agenda-item/${agendaItem.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/agenda-item/${agendaItem.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="meetingsApp.agendaItem.home.notFound">No Agenda Items found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default AgendaItem;
