#mysql -u hks -phks_pa55word < ddl.sql
#CREATE USER 'hks'@'localhost' IDENTIFIED BY 'hks_pa55word';
drop database hks;
create database hks;
GRANT ALL PRIVILEGES ON hks.* TO 'hks'@'localhost';
use hks;
create table Game (
    id integer not null auto_increment,
    created datetime not null,
    updated datetime not null,
    betrayCaughtChance double precision not null,

    betrayalCost integer not null,
    feedbackBonus float not null,
    exchangeRate float not null,
    initialTrusterBonus integer not null,
    maxRoundsNum integer not null,

    blackMarkUpperLimit integer not null,
    gameId varchar(255) not null,
    initTempteeBonus integer not null,
    maxBetrayPayoff integer not null,
    numberOfGameSlot integer not null,
    rewardCaughtAsBetrayalChance double precision not null,
    rewardPayoff integer not null,
    tempteeSurvivalChance double precision not null,
    primary key (id)
);

create table Slot (
    id integer not null auto_increment,
    created datetime not null,
    updated datetime not null,
    assignmentId varchar(255),
    betrayCaught bit not null,
    betrayCaughtChance double precision not null,
    betrayCaughtSampling double precision not null,
    blackMarkCount integer not null,
    blackMarkUpperLimit integer not null,
    currentBetrayPayoff integer not null,
    currentRound integer not null,
    hitId varchar(255),
    initTempteeBonus integer not null,
    lastAction varchar(255),
    log longblob,
    playerReportJson TEXT,
    maxBetrayPayoff integer not null,
    rewardCaughtAsBetrayal bit not null,
    rewardCaughtAsBetrayalChance double precision not null,
    rewardCaughtAsBetrayalSampling double precision not null,
    rewardPayoff integer not null,
    slotId varchar(255),
    slotNumber integer not null,
    status varchar(255) not null,
    practice boolean,
    survival bit not null,
    survivalSampling double precision not null,
    tempteeBonus integer not null,
    tempteeSurvivalChance double precision not null,
    turkSubmitTo longtext,
    tutorialStep integer not null,
    workerId varchar(255),
    workerNumber varchar(255),
    workerPlayTracker integer not null,
    game_id integer not null,
    primary key (id)
);

create table Feedback (
    id integer not null auto_increment,
    created datetime not null,
    updated datetime not null,
    age integer not null,
    gender integer not null,
    nativeLanguage integer not null,
    instruction integer not null,
    interesting integer not null,
    speed integer not null,
    strategy varchar(255),
    thoughts varchar(255),
    game_id integer not null,
    slot_id integer not null,
    primary key (id)
);

alter table Slot 
    add index FK275E1EE987676F (game_id), 
    add constraint FK275E1EE987676F 
    foreign key (game_id) 
    references Game (id);


alter table Feedback 
    add index FKF8704FA5210D3108 (game_id), 
    add constraint FKF8704FA5210D3108 
    foreign key (game_id) 
    references Game (id);

alter table Feedback 
    add index FKF8704FA5958BB4EA (slot_id), 
    add constraint FKF8704FA5958BB4EA 
    foreign key (slot_id) 
    references Slot(id);
