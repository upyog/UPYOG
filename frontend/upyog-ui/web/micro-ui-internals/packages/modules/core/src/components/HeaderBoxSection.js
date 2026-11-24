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
						const isCentered = !isServiceVariant && typeof card?.icon === "string" && card.icon.includes("+");
						const themeClass = card?.theme ? `buttonCard--service-${card.theme}` : "";

						return (
							<button
								type="button"
								key={`${card.title || "card"}-${index}`}
								onClick={card.onClick}
								className={`buttonCard ${isServiceVariant ? "buttonCard--service" : ""} ${themeClass} ${isCentered ? "buttonCard--center" : ""} ${card.accent ? `buttonCard--${card.accent}` : ""}`}
							>
								{isServiceVariant ? (
									<>
										<div className="serviceCardTopRow">
											<div className="serviceVarientIcon">
												{card.svgIcon && (
													<span className="serviceVarientIcon__svg">{card.svgIcon}</span>
												)}
												<span className="serviceVarientIcon__code">
													{card.badgeCode || card.icon || card.title?.slice(0, 2)?.toUpperCase() || "•"}
												</span>
											</div>
											<div className="serviceCardTopMeta">
												<span className="serviceCardArrowCircle">→</span>
											</div>
										</div>

										<div className="serviceCardContent">
											<div className="serviceVarientsubTitle">
												{card.subtitle}
											</div>
											{card.subtitle ? (
												<div className="serviceVarientText">
													{card.text}
												</div>
											) : null}
										</div>

										{card.ctaLabel && (
											<div className="serviceCardFooterCta">
												<span>{card.ctaLabel}</span>
												<span className="serviceCardFooterCta__arrow">→</span>
											</div>
										)}

										{card.svgIcon && (
											<div className="serviceCardBgWatermark" aria-hidden="true">
												{card.svgIcon}
											</div>
										)}
									</>
								) : (
									<>
										<div className="statCardHeaderRow">
											<div className="serviceVarientTitle">
												{card.title}
											</div>
											{card.badgeIcon && (
												<span className="statCardBadgeIcon">{card.badgeIcon}</span>
											)}
										</div>

										<div className="statCardValue">
											{card.value}
										</div>

										{card.actionLabel && card.value !== 0 ? (
											<div className="statCardAction">
												{card.actionLabel} <span>→</span>
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
