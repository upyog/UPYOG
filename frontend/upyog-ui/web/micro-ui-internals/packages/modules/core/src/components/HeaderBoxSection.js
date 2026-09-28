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

				<div
					className="sectionBody"
					style={{gridTemplateColumns}}
				>
					{cards.map((card, index) => {
						const isServiceVariant = variant === "service";

						return (
							<button
								type="button"
								key={`${card.title || "card"}-${index}`}
								onClick={card.onClick}
								className="buttonCard"
								style={{
									alignItems: card?.icon?.includes("+") ? "center" : "",
									textAlign: card?.icon?.includes("+") ? "center" : "left",
								}}
							>
								{isServiceVariant ? (
									<>
										<div className="serviceVarientIcon">
											{card.icon || card.title?.slice(0, 2)?.toUpperCase() || "•"}
										</div>

										<div>
											<div
											className="serviceVarientsubTitle"
											>
												{card.subtitle}
											</div>
											{card.subtitle ? (
												<div
													style={{
														fontSize: "12px",
														color: "#5b5866",
														lineHeight: "1.4",
													}}
												>
													{card.text}
												</div>
											) : null}
										</div>
									</>
								) : (
									<>
										<div
										className="serviceVarientTitle"
										>
											{card.title}
										</div>

										<div
											style={{
												fontSize: "32px",
												fontWeight: 800,
												lineHeight: 1,
												color: "#1f1b2d",
											}}
										>
											{card.value}
										</div>

										{card.actionLabel && card.value !== 0 ? (
											<div
												style={{
													marginTop: "18px",
													fontSize: "13px",
													fontWeight: 600,
													color: "#1f1b2d",
													textDecoration: "underline",
													textUnderlineOffset: "2px",
												}}
											>
												{card.actionLabel}
											</div>
										) : <div
											style={{
												marginTop: "18px",
												fontSize: "13px",
												fontWeight: 600,
												color: "#7F7797",
											}}
										>
											{"No task assigned"}
										</div>}
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
