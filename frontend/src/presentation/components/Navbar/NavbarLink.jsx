import { Link } from "react-router";
import { Box, Typography } from "@mui/material";

const NavbarLink = ({ label, route, handler }) => {
  return (
    <Typography
      component={Link}
      to={route}
      onClick={handler}
      color="info.main"
      sx={{
        paddingTop: "10px",
        paddingBottom: "10px",
        textDecoration: "none",
        fontFamily: '"Play", serif',
        fontSize: "1.1rem",
        fontWeight: 700,
        fontStyle: "normal",
        borderBottom: "3px solid transparent",
        "&:hover": {
          color: "info.light",
          // borderBottom: '3px solid orange'
        },
      }}
    >
      {label}
    </Typography>
  );
};

export default NavbarLink;
