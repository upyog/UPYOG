import React from "react";
import { UserActionCard } from "@nudmcdgnpm/digit-ui-react-components";

const UserActionLayout = ({ cards = [], showEmpty = false }) => {
  if (showEmpty) {
    return (
      <div className="userActionEmptyState">
        <img src="/images/Main.png" alt="Empty" />
        <h1>Nothing here yet!</h1>
        <p>Your applications, tasks or notifications will appear here, once you start using UPYOG</p>
      </div>
    );
  }

  return (
    <div className="userActionCardGrid">
      {cards.map((card, index) => (
        <UserActionCard key={`${card.title}-${index}`} {...card} />
      ))}
    </div>
  );
};

export default UserActionLayout;
