import React from "react";

const HeaderBoxSection = ({ title, actionLabel, onActionClick, cards = [], variant = "stats", columns = 4 }) => {
	const gridTemplateColumns = `repeat(${Math.min(cards.length || 1, columns)}, minmax(0, 1fr))`;

	return (
		<>
			<div className="headerBoxSection">
				{(title || actionLabel) && (
					<div className="sectionTitle">
						{title ? (
							<h3>{title}</h3>
						) : null}
						{actionLabel ? (
							<button
								type="button"
								onClick={onActionClick}
							>
								{actionLabel}
							</button>
						) : null}
					</div>
				)}

				<div className="sectionBody">
					{cards.map((card, index) => {
						const isServiceVariant = variant === "service";
						const isCentered = card?.icon?.includes("+");

						return (
							<button
								type="button"
								key={`${card.title || "card"}-${index}`}
								onClick={card.onClick}
								className={`buttonCard ${isCentered ? "buttonCard--center" : ""}`}
							>
								{isServiceVariant ? (
									<>
										<div className="serviceVarientIcon">
											{card.icon || card.title?.slice(0, 2)?.toUpperCase() || "•"}
										</div>

										<div>
											<div className="serviceVarientsubTitle">
												{card.subtitle}
											</div>
											{card.subtitle ? (
												<div className="serviceVarientText">
													{card.text}
												</div>
											) : null}
										</div>
									</>
								) : (
									<>
										<div className="serviceVarientTitle">
											{card.title}
										</div>

										<div className="statCardValue">
											{card.value}
										</div>

										{card.actionLabel && card.value !== 0 ? (
											<div className="statCardAction">
												{card.actionLabel}
											</div>
										) : (
											<div className="statCardNoTask">
												{"No task assigned"}
											</div>
										)}
									</>
								)}
							</button>
						);
					})}
				</div>
			</div>
		</>
	);
};

export default HeaderBoxSection;
