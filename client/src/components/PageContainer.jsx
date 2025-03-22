import { Box } from '@mui/material';

const PageContainer = ({ children }) => {
  return (
    <Box 
      sx={{
        p: 6,
        minHeight: 'calc(100vh - 64px - 32px)',
        backgroundColor: '#f5f5f5'
      }}
    >
      {children}
    </Box>
  );
};

export default PageContainer;