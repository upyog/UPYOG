import React from "react";
import { UserActionCard } from "@nudmcdgnpm/digit-ui-react-components";

const UserActionLayout = ({ cards = [], showEmpty = false }) => {
  if (showEmpty) {
    return (
      <div
        style={{
          height: "250px",
          background: "#fff",
          display: "flex",
          alignItems: "center",
          flexDirection: "column",
          border: "1px solid #D6CDE4",
          borderRadius: "6px",
          marginBottom: "20px",
          justifyContent: "center",
        }}
      >
        <img src="/images/Main.png" />
        <h1 style={{ fontSize: "22px", fontWeight: 600 }}>Nothing here yet!</h1>
        <p>Your applications, tasks or notifications will appear here, once you start using UPYOG</p>
      </div>
    );
  }

  return (
    <div
      className="userActionCard"
      style={{
        display: "grid",
        gridTemplateColumns: "repeat(3, minmax(0, 1fr))",
        gap: "20px",
        margin: "28px 0px",
        width: "100%",
      }}
    >
      {cards.map((card, index) => (
        <UserActionCard key={`${card.title}-${index}`} {...card} />
      ))}
    </div>
  );
};

export default UserActionLayout;
