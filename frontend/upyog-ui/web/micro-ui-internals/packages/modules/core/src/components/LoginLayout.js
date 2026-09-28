import React from "react";

const LoginLayout = ({ children, heroContent, footer, layout }) => {
	const { backgroundUrl = "", mainlogo = {}, headers = {}, pointers = {}, eGovLogos = {}, CFooter = {}, isEmployeeV2 = false } = layout?.loginUI || {};
	const { isFormCentered = false, isTopIconReq = false } = layout?.loginForm || {};
	const isEmployeeNew = isEmployeeV2;
	const footerItems = [
		"Property tax",
		"Trade Licence",
		"Building Plan Approval",
		"Birth and Death Certificate",
		"and many more...",
	];
	const shellStyle = backgroundUrl ? { backgroundImage: `url(${backgroundUrl})` } : {};
	const innerShellStyle = mainlogo?.styles ? { ...mainlogo?.styles } : {}
	const dualImageView = mainlogo.dualLogo && !mainlogo.verticalView ? { flexDirection: "row-reverse" } : {}
	const dualVerticalView = mainlogo.verticalView ? { flexDirection: "column" } : null

	return (
		<>
			{isEmployeeNew && <div className="login-navbar">
				<div className="login-navbar-card">
					<div className="cards card1">
					<img src="/images/logo-secondary.png"/>
					</div>
					<div className="cards card2">
					<img src="/images/circular.png"/>
					</div>
				</div>
				<div className="login-navbar-card">
					<div  className="cards card3">
					<img src="/images/yogi.png"/>
					<div className="guest-label">
						<p>Yogi Adityanath</p>
						<p>Honorable Chief Minister</p>
						<p>Uttar Pradesh</p>
					</div>
					</div>
					<div  className="cards card4">
					<img src="/images/modi.png"/>
					<div className="guest-label">
						<p>Narendra Modi</p>
						<p>Honorable Prime Minister</p>
						<p>Government of India</p>
					</div>
					</div>
				</div>
				</div>}
			<div className={`login-shell ${isEmployeeNew ? "login-shell-employeeV2" : ""} `} style={shellStyle}>
				<aside className="login-hero" style={innerShellStyle}>
					<div className="login-hero__content">
						{!isEmployeeNew && <a className="login-brand" style={dualVerticalView || dualImageView} href="/upyog-ui/citizen" title="Urban Platform for deliverY of Online Governance">
							{<img className={mainlogo.verticalView ? "verticalView" : ""} src={mainlogo.url} alt="UPYOG Logo" />}
							{mainlogo?.dualLogo && <div className="dualImages"><img style={mainlogo?.url2Styles ? { ...mainlogo?.url2Styles } : {}} src={mainlogo.url2} alt="secondary Logo" /></div>}
						</a>}

						<h1 className="login-title" style={{ ...headers?.H1Styles }}>
							{headers?.H1} <span style={{ ...headers?.H2Styles }}>{headers?.H2}</span>
						</h1>

						<p className="login-subtitle" style={{ ...headers?.sHStyles }}>
							{headers.sH1} <span>{headers.sH2}</span>
						</p>

						{headers?.secondaryHeader && <p className="secondaryHead">{headers?.secondaryHeader}</p>}

						{pointers?.isReq && <ul className={`login-features ${isEmployeeNew ? "login-features-emp" : ""} `}>
							{pointers?.data?.map((pointer, idx) => (
								<li key={idx} className={`login-feature ${!(pointers?.isbackground) ? "noBackground" : ""}`}>
									{pointers?.isIcon && <div className="login-feature__icon">
										<img src={pointer?.iconURL} alt={pointer?.iconAlt} />
									</div>}
									<div className="login-feature__info">
										<h3>{pointer?.key1}</h3>
										<p>{pointer?.key2}</p>
									</div>
								</li>
							))}
						</ul>}

						{eGovLogos?.isLogoReq && <div className="egovLogos">
							{eGovLogos?.logoURLList?.map((logo, idx) => (
								<img src={logo} alt="logos" />
							))}
						</div>}
					</div>
				</aside>
				<div className={`login-app-container ${isFormCentered ? "login-app-containerv2" : ""} `}>
					{isTopIconReq && <div class="centeredLogo">
						<img src="/images/centered.svg" alt="Logo" />
					</div>}
					<div className="login-panel__content">{children}</div>
					{footer}
				</div>
			</div>
			{CFooter?.isFooter && <div className="footerArea" style={{ ...CFooter?.footerStyles }}>
				<div className="footerShell">
					{footerItems.map((item, idx) => (
						<div key={item}>{item}</div>
					))}
				</div>
			</div>}
		</>
	);
};

export default LoginLayout;
