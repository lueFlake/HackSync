import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import moment from 'moment';
import { Button, Select } from 'antd';
import { LeftOutlined, RightOutlined } from '@ant-design/icons';
import '../styles/calendar.css';
import PageContainer from '../components/PageContainer';
import { Typography } from '@mui/material';
import 'moment/locale/ru';

moment.locale('ru');

const CalendarPage = () => {
  const { yearMonth } = useParams();
  const navigate = useNavigate();
  const [currentDate, setCurrentDate] = useState(moment());
  const [events] = useState([
    { date: '2034-05-05', type: 'participating' },
    { date: '2034-05-15', type: 'open' },
    { date: '2034-05-25', type: 'closed' },
  ]);
  const [years, setYears] = useState([]);

  // Генерация списка годов
  useEffect(() => {
    const startYear = 2010;
    const endYear = 2035;
    const yearsArray = [];
    for (let year = startYear; year <= endYear; year++) {
      yearsArray.push(year);
    }
    setYears(yearsArray);
  }, []);

  // Синхронизация с URL
  useEffect(() => {
    if (yearMonth) {
      const date = moment(yearMonth, 'YYYY-MM');
      if (date.isValid()) {
        setCurrentDate(date.startOf('month'));
      } else {
        navigate(`/calendar/${moment().format('YYYY-MM')}`);
      }
    }
  }, [yearMonth, navigate]);

  // Синхронизация с URL и валидация параметров
  useEffect(() => {
    if (yearMonth) {
      const date = moment(yearMonth, 'YYYY-MM');
      if (date.isValid()) {
        setCurrentDate(date.startOf('month'));
      } else {
        navigate(`/calendar/${moment().format('YYYY-MM')}`);
      }
    }
  }, [yearMonth, navigate]);

  // Переключение месяцев
  const changeMonth = (direction) => {
    const newDate = currentDate.clone().add(direction, 'months');
    navigate(`/calendar/${newDate.format('YYYY-MM')}`);
  };

  const changeYear = (year) => {
    const newDate = currentDate.clone().year(year);
    navigate(`/calendar/${newDate.format('YYYY-MM')}`);
  };

  // Генерация сетки календаря
  const renderCalendar = () => {
    const startOfMonth = currentDate.clone().startOf('month').startOf('week');
    const endOfMonth = currentDate.clone().endOf('month').endOf('week');
    
    const weeks = [];
    let day = startOfMonth.clone();

    while (day.isBefore(endOfMonth)) {
      const week = [];
      for (let i = 0; i < 7; i++) {
        week.push(day.clone());
        day.add(1, 'day');
      }
      weeks.push(week);
    }

    return weeks.map((week, weekIndex) => (
      <div key={weekIndex} className="calendar-week">
        {week.map((date, dayIndex) => {
          const isCurrentMonth = date.month() === currentDate.month();
          const isToday = date.isSame(moment(), 'day');
          const event = events.find(e => e.date === date.format('YYYY-MM-DD'));

          return (
            <div 
              key={dayIndex}
              className={`calendar-day ${isCurrentMonth ? 'current-month' : ''} ${isToday ? 'today' : ''}`}
              onClick={() => handleDateClick(date)}
            >
              <div className="day-number">{date.date()}</div>
              {event && <div className={`event-dot ${event.type}`} />}
            </div>
          );
        })}
      </div>
    ));
  };

  const handleDateClick = (date) => {
    console.log('Выбрана дата:', date.format('YYYY-MM-DD'));
  };

  return (
    <PageContainer>
    <Typography variant="h4" gutterBottom>
        Календарь событий
    </Typography>
    <div className="calendar-container">
    <div className="calendar-header">
      <div className="year-selector">
        Год: 
        <Select
          value={currentDate.year()}
          onChange={changeYear}
          options={years.map(year => ({
            value: year,
            label: year
          }))}
          style={{ width: 120 }}
        />
      </div>
        
      <div className="month-navigation">
        <Button 
          type="primary" 
          shape="circle" 
          icon={<LeftOutlined />} 
          onClick={() => changeMonth(-1)}
        />
        <h2>{capitalizeFirstLetter(currentDate.format('MMMM'))} {currentDate.format('YYYY')}</h2>
        <Button 
          type="primary" 
          shape="circle" 
          icon={<RightOutlined />} 
          onClick={() => changeMonth(1)}
        />
      </div>
      </div>
      
      <div className="legend">
        <div className="legend-item">
          <div className="event-dot participating" />
          <span>Участвую</span>
        </div>
        <div className="legend-item">
          <div className="event-dot open" />
          <span>Регистрация открыта</span>
        </div>
        <div className="legend-item">
          <div className="event-dot closed" />
          <span>Регистрация закрыта</span>
        </div>
        <div className="legend-item">
          <div className="today-marker" />
          <span>Сегодня</span>
        </div>
      </div>
      
      <div className="calendar-days-header">
        {['Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб'].map(day => (
          <div key={day} className="day-header">{day}</div>
        ))}
      </div>
      
      <div className="calendar-grid">
        {renderCalendar()}
      </div>
    </div>
    </PageContainer>
  );
};

const capitalizeFirstLetter = (string) => {
  return string.charAt(0).toUpperCase() + string.slice(1);
};

export default CalendarPage;