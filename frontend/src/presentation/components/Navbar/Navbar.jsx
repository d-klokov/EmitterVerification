import {
  AppBar,
  Toolbar,
  Stack,
  useTheme,
  useMediaQuery,
  IconButton,
  Dialog,
  DialogContent,
  Divider,
} from "@mui/material";
import NavbarLink from "./NavbarLink";
import { useState } from "react";
import { Menu, Close } from "@mui/icons-material";

const Navbar = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  const [open, setOpen] = useState(false);

  const openHandler = () => {
    setOpen(true);
  };

  const closeHandler = () => {
    setOpen(false);
  };

  return (
    <>
      <AppBar position="static" color={"transparent"} elevation={0}>
        <Toolbar sx={{ px: 2, height: "100%" }}>
          {isMobile ? (
            <Stack
              direction="row"
              sx={{
                width: "100%",
                justifyContent: "end",
                alignItems: "center",
              }}
            >
              <IconButton
                color="info.dark"
                aria-label="menu"
                onClick={openHandler}
              >
                <Menu />
              </IconButton>
            </Stack>
          ) : (
            <Stack direction={"row"} spacing={2} sx={{ textAlign: "center" }}>
              <NavbarLink label={"Излучатели"} route={"/emitters-list"} />
              <NavbarLink label={"Проверки"} />
              <NavbarLink label={"Типы"} route={"/types-list"} />
              <NavbarLink label={"Владельцы"} route={"/owners-list"} />
            </Stack>
          )}
        </Toolbar>
        <Divider />
      </AppBar>

      <Dialog
        open={open}
        onClose={closeHandler}
        fullScreen
        fullWidth
        hideBackdrop
      >
        <AppBar position="static" color={"transparent"} elevation={0}>
          <Toolbar sx={{ px: 2, height: "100%" }}>
            <Stack
              direction="row"
              sx={{
                width: "100%",
                justifyContent: "end",
                alignItems: "center",
              }}
            >
              <IconButton
                color="info.dark"
                aria-label="menu"
                onClick={closeHandler}
              >
                <Close />
              </IconButton>
            </Stack>
          </Toolbar>
          <Divider />
        </AppBar>
        <DialogContent>
          <Stack spacing={1} sx={{ textAlign: "center", width: "100%" }}>
            <NavbarLink
              label={"Излучатели"}
              route={"/emitters-list"}
              handler={closeHandler}
            />
            <NavbarLink label={"Проверки"} handler={closeHandler} />
            <NavbarLink
              label={"Типы"}
              route={"/types-list"}
              handler={closeHandler}
            />
            <NavbarLink
              label={"Владельцы"}
              route={"/owners-list"}
              handler={closeHandler}
            />
          </Stack>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default Navbar;
