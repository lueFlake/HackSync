import { useCallback } from 'react';
import ApiService from '../services/ApiService';

export const useApi = () => {
    const get = useCallback(async (endpoint, useCache = true) => {
        const response = await ApiService.request(endpoint, 'GET', null, true, ApiService.DEFAULT_TIMEOUT, useCache);
        return { data: response };
    }, []);

    const post = useCallback(async (endpoint, data) => {
        const response = await ApiService.request(endpoint, 'POST', data, true, ApiService.DEFAULT_TIMEOUT);
        return { data: response };
    }, []);

    const put = useCallback(async (endpoint, data) => {
        const response = await ApiService.request(endpoint, 'PUT', data, true, ApiService.DEFAULT_TIMEOUT);
        return { data: response };
    }, []);

    const del = useCallback(async (endpoint) => {
        const response = await ApiService.request(endpoint, 'DELETE', null, true, ApiService.DEFAULT_TIMEOUT);
        return { data: response };
    }, []);

    return {
        get,
        post,
        put,
        delete: del
    };
}; 