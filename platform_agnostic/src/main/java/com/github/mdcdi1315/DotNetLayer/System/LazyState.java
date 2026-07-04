package com.github.mdcdi1315.DotNetLayer.System;

enum LazyState
{
    NoneViaConstructor,
    NoneViaFactory,
    NoneException,

    PublicationOnlyViaConstructor,
    PublicationOnlyViaFactory,
    PublicationOnlyWait,
    PublicationOnlyException,

    ExecutionAndPublicationViaConstructor,
    ExecutionAndPublicationViaFactory,
    ExecutionAndPublicationException
}
