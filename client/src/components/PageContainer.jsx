import { Box, Paper, Typography } from "@mui/material";
import { styled } from "@mui/material/styles";

const Container = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(1),
  display: "flex",
  flexDirection: "column",
}));

const PageContainer = ({ children, title }) => {
  return (
    <Box
      sx={{
        p: 1,
        paddingTop: 6,
        minHeight: "calc(100vh - 64px - 32px)",
        backgroundColor: "#f5f5f5",
      }}
    >
      <Container sx={{ m: 0.5 }}>
        <Typography
          fontWeight="bold"
          style={{ fontSize: 24, display: "block" }}
        >
          {title}
        </Typography>
      </Container>
      {children}
    </Box>
  );
};

export default PageContainer;
