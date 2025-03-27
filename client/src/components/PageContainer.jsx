import { Typography, Box } from '@mui/material';

const PageContainer = ({ children, title }) => {
  return (
    <Box 
      sx={{
        p: 6,
        minHeight: 'calc(100vh - 64px - 32px)',
        backgroundColor: '#f5f5f5'
      }}
    >
      <Typography strong style={{ fontSize: 24, display: 'block', marginBottom: 16 }}>
        {title}
      </Typography>
      {children}
    </Box>
  );
};

export default PageContainer;