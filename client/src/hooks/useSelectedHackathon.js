import { useEffect, useState } from 'react';
import { useApi } from './useApi';

export const useSelectedHackathon = () => {
    const [selectedHackathon, setSelectedHackathon] = useState(null);
    const api = useApi();

    useEffect(() => {
        const storedId = localStorage.getItem('selectedEventId');
        if (storedId) {
            loadHackathon(storedId);
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

    return { selectedHackathon, setSelectedHackathonId };
}; 