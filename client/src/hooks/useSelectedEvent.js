import { useEffect, useState } from 'react';
import { useApi } from './useApi';

export const useSelectedHackathon = () => {
    const [selectedHackathon, setSelectedHackathon] = useState(null);
    const [selectedTeamId, setSelectedTeamId] = useState(null);
    const api = useApi();

    useEffect(() => {
        const storedEventId = localStorage.getItem('selectedEventId');
        const storedTeamId = localStorage.getItem('selectedTeamId');
        if (storedEventId) {
            loadHackathon(storedEventId);
        }
        if (storedTeamId) {
            setSelectedTeamId(storedTeamId);
        }
    }, []);

    const loadHackathon = async (id) => {
        try {
            const response = await api.get(`/hackathons/${id}`);
            setSelectedHackathon(response.data);
        } catch (error) {
            console.error('Failed to load selected hackathon:', error);
            localStorage.removeItem('selectedEventId');
            setSelectedHackathon(null);
        }
    };

    const setSelectedHackathonId = (id) => {
        if (id) {
            localStorage.setItem('selectedEventId', id);
            loadHackathon(id);
        } else {
            localStorage.removeItem('selectedEventId');
            setSelectedHackathon(null);
        }
    };

    const updateSelectedTeamId = (id) => {
        if (id) {
            localStorage.setItem('selectedTeamId', id);
            setSelectedTeamId(id);
        } else {
            localStorage.removeItem('selectedTeamId');
            setSelectedTeamId(null);
        }
    };

    return {
        selectedHackathon,
        selectedTeamId,
        setSelectedHackathonId,
        updateSelectedTeamId
    };
}; 